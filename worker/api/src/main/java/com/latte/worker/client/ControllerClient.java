package com.latte.worker.client;

import com.latte.worker.config.WorkerConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RefreshScope
public class ControllerClient {
    private final WebClient webClient;

    public ControllerClient(WorkerConfig workerConfig) {
        webClient = WebClient.builder()
                .baseUrl(workerConfig.getController().getUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public Mono<Void> reportSummary(String testName, LocalDateTime startTime, String branchName, String scriptFilePath, String summary) {
        Map<String, Object> body = new HashMap<>();
        body.put("testName", testName);
        body.put("startTime", startTime);
        body.put("branchName", branchName);
        body.put("scriptFilePath", scriptFilePath);
        body.put("summary", summary);

        return webClient.method(HttpMethod.POST)
                .uri("/history")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Void.class)
                .doOnError(throwable -> log.error("Failed to report the summary", throwable));
    }
}
