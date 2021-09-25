package com.latte.worker.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class ControllerClient {
    private final WebClient webClient;

    public ControllerClient() {
        /* TODO: Use configuration for url */
        webClient = WebClient.builder()
                .baseUrl("http://localhost:8080")
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
