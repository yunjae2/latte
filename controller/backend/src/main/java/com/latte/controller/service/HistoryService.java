package com.latte.controller.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.latte.controller.controller.request.HistorySaveRequest;
import com.latte.controller.domain.Latency;
import com.latte.controller.domain.TestHistory;
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

    public Mono<Void> save(HistorySaveRequest historySaveRequest) {
        TestHistory testHistory = convertToTestHistory(historySaveRequest);
        return Mono.fromRunnable(() -> historyRepository.save(testHistory));
    }

    private TestHistory convertToTestHistory(HistorySaveRequest historySaveRequest) {
        JsonNode root;
        try {
            root = objectMapper.readTree(historySaveRequest.getSummary());
        } catch (JsonProcessingException e) {
            log.error("Failed to parse summary results", e);
            throw new IllegalStateException(e);
        }

        return TestHistory.builder()
                .name(historySaveRequest.getTestName())
                .date(historySaveRequest.getStartTime())
                .branchName(historySaveRequest.getBranchName())
                .scriptFilePath(historySaveRequest.getScriptFilePath())
                .isSuccessful(true)     // TODO
                .requestCount(root.at("/metrics/http_reqs/values/count").asLong())
                .rps(root.at("/metrics/http_reqs/values/rate").asDouble())
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
                .result(historySaveRequest.getSummary())
                .build();
    }
}
