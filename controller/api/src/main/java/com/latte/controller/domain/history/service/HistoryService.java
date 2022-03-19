package com.latte.controller.domain.history.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.latte.controller.domain.Latency;
import com.latte.controller.domain.TestHistory;
import com.latte.controller.domain.TestHistory.TestHistoryBuilder;
import com.latte.controller.domain.history.infrastructure.HistoryRepository;
import com.latte.controller.domain.history.interfaces.request.HistorySearchRequest;
import com.latte.controller.dto.RunConfig;
import com.latte.controller.dto.RunInfo;
import com.latte.controller.repository.LogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class HistoryService {
    private final HistoryRepository historyRepository;
    private final LogRepository logRepository;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public Mono<Long> count() {
        return Mono.fromCallable(() -> historyRepository.count())
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<List<TestHistory>> getAll() {
        return Mono.fromCallable(() -> historyRepository.findAll())
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<List<TestHistory>> fetch(HistorySearchRequest historySearchRequest) {
        return Mono.fromCallable(() -> historyRepository.findAll(buildPageRequest(historySearchRequest)))
                .map(Slice::getContent)
                .subscribeOn(Schedulers.boundedElastic());
    }

    private PageRequest buildPageRequest(HistorySearchRequest historySearchRequest) {
        return PageRequest.ofSize(historySearchRequest.getSize())
                .withPage(historySearchRequest.getPage())
                .withSort(Direction.fromString(historySearchRequest.getOrder().name()), historySearchRequest.getOrderBy());
    }

    public Mono<Void> save(RunConfig runConfig, RunInfo runInfo, String summary, String consoleLog) {
        return Mono.fromCallable(() -> logRepository.save(consoleLog))
                .map(logPath -> buildTestHistory(runConfig, runInfo, summary, logPath))
                .map(historyRepository::save)
                .subscribeOn(Schedulers.boundedElastic())
                .doOnSuccess(v -> log.info("Run history saved successfully"))
                .then();
    }

    private TestHistory buildTestHistory(RunConfig runConfig, RunInfo runInfo, String summary, String logPath) {
        return fillSummaryData(TestHistory.builder(), summary)
                .name(runInfo.getTestName())
                .date(runInfo.getStartTime())
                .branchName(runConfig.getBranchName())
                .scriptFilePath(runConfig.getScriptFilePath())
                .isSuccessful(true)     // TODO
                .requestedTps(runConfig.getTps())
                .result(summary)
                .logPath(logPath)
                .build();
    }

    public Mono<Boolean> delete(Long id) {
        return Mono.fromRunnable(() -> historyRepository.deleteById(id))
                .subscribeOn(Schedulers.boundedElastic())
                .thenReturn(true);
    }

    public Mono<TestHistory> get(Long id) {
        return Mono.fromCallable(() -> historyRepository.findById(id))
                .subscribeOn(Schedulers.boundedElastic())
                .map(testHistory -> testHistory.orElseThrow(() -> new IllegalStateException("Failed to get the test history of id " + id)));
    }

    @Transactional
    public Mono<TestHistory> updateName(Long id, String name) {
        return Mono.fromCallable(() -> historyRepository.findById(id))
                .map(optional -> optional.orElseThrow(() -> new IllegalStateException("Failed to get the test history of id " + id)))
                .doOnNext(testHistory -> testHistory.setName(name))
                .map(testHistory -> historyRepository.save(testHistory))
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Void> rewriteAll() {
        return Mono.fromCallable(historyRepository::findAll)
                .map(testHistories -> testHistories.stream()
                        .map(history -> fillSummaryData(history.toBuilder(), history.getResult()).build())
                        .collect(Collectors.toList()))
                .map(historyRepository::saveAllAndFlush)
                .then();
    }

    private TestHistoryBuilder fillSummaryData(TestHistoryBuilder builder, String summary) {
        JsonNode root = getJsonRoot(summary);
        return builder
                .successCount(root.at("/metrics/http_req_failed/values/fails").asLong())
                .failCount(root.at("/metrics/http_req_failed/values/passes").asLong())
                .requestCount(root.at("/metrics/http_reqs/values/count").asLong())
                .actualTps(root.at("/metrics/iterations/values/rate").asDouble())
                .iterationTotal(root.at("/metrics/iterations/values/count").asLong())
                .iterationFail(root.at("/metrics/fail_counter/values/count").asLong())
                .duration(root.at("/state/testRunDurationMs").asDouble())
                .latency(Latency.builder()
                        .max(root.at("/metrics/http_req_duration/values/max").asDouble())
                        .min(root.at("/metrics/http_req_duration/values/min").asDouble())
                        .avg(root.at("/metrics/http_req_duration/values/avg").asDouble())
                        .p50(root.at("/metrics/http_req_duration/values/p(50)").asDouble())
                        .p99(root.at("/metrics/http_req_duration/values/p(99)").asDouble())
                        .p9c3(root.at("/metrics/http_req_duration/values/p(99.9)").asDouble())
                        .p9c4(root.at("/metrics/http_req_duration/values/p(99.99)").asDouble())
                        .build());
    }

    private JsonNode getJsonRoot(String summary) {
        try {
            return objectMapper.readTree(summary);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse summary results", e);
            throw new IllegalStateException(e);
        }
    }

    public Mono<Resource> getLog(Long id) {
        return this.get(id)
                .map(TestHistory::getLogPath)
                .map(logRepository::getLog)
                .subscribeOn(Schedulers.boundedElastic());
    }

}
