package com.latte.controller.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.latte.controller.domain.Latency;
import com.latte.controller.domain.TestHistory;
import com.latte.controller.domain.TestHistory.TestHistoryBuilder;
import com.latte.controller.dto.RunConfig;
import com.latte.controller.dto.RunInfo;
import com.latte.controller.repository.HistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class HistoryService {
    private final HistoryRepository historyRepository;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public Mono<List<TestHistory>> getAll() {
        return Mono.just(historyRepository.findAll());
    }

    public Mono<Void> save(RunConfig runConfig, RunInfo runInfo, String summary) {
        TestHistory testHistory = buildTestHistory(runConfig, runInfo, summary);
        return Mono.fromRunnable(() -> {
            historyRepository.save(testHistory);
            log.info("Run history saved successfully");
        });
    }

    private TestHistory buildTestHistory(RunConfig runConfig, RunInfo runInfo, String summary) {
        return fillSummaryData(TestHistory.builder(), summary)
                .name(runInfo.getTestName())
                .date(runInfo.getStartTime())
                .branchName(runConfig.getBranchName())
                .scriptFilePath(runConfig.getScriptFilePath())
                .isSuccessful(true)     // TODO
                .requestedTps(runConfig.getTps())
                .result(summary)
                .build();
    }

    public Mono<Boolean> delete(Long id) {
        return Mono.fromRunnable(() -> historyRepository.deleteById(id))
                .thenReturn(true);
    }

    public Mono<TestHistory> get(Long id) {
        return Mono.fromCallable(() -> historyRepository.findById(id))
                .map(testHistory -> testHistory.orElseThrow(() -> new IllegalStateException("Failed to get the test history of id " + id)));
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
                        .p99_9(root.at("/metrics/http_req_duration/values/p(99.9)").asDouble())
                        .p99_99(root.at("/metrics/http_req_duration/values/p(99.99)").asDouble())
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
}
