package com.latte.controller.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.latte.controller.domain.Latency;
import com.latte.controller.domain.TestHistory;
import com.latte.controller.dto.RunConfig;
import com.latte.controller.dto.RunInfo;
import com.latte.controller.repository.HistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

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
        JsonNode root;
        try {
            root = objectMapper.readTree(summary);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse summary results", e);
            throw new IllegalStateException(e);
        }

        return TestHistory.builder()
                .name(runInfo.getTestName())
                .date(runInfo.getStartTime())
                .branchName(runConfig.getBranchName())
                .scriptFilePath(runConfig.getScriptFilePath())
                .isSuccessful(true)     // TODO
                .requestCount(root.at("/metrics/http_reqs/values/count").asLong())
                .requestedTps(runConfig.getTps())
                .actualTps(root.at("/metrics/http_reqs/values/rate").asDouble())
                .duration(root.at("/state/testRunDurationMs").asDouble())
                .latency(Latency.builder()
                        .max(root.at("/metrics/http_req_duration/values/max").asDouble())
                        .min(root.at("/metrics/http_req_duration/values/min").asDouble())
                        .avg(root.at("/metrics/http_req_duration/values/avg").asDouble())
                        .p50(root.at("/metrics/http_req_duration/values/p(50)").asDouble())
                        .p99(root.at("/metrics/http_req_duration/values/p(99)").asDouble())
                        .p99_9(root.at("/metrics/http_req_duration/values/p(99.9)").asDouble())
                        .p99_99(root.at("/metrics/http_req_duration/values/p(99.99)").asDouble())
                        .build())
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
}
