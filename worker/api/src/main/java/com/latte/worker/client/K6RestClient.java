package com.latte.worker.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class K6RestClient {
    private final WebClient webClient;
    private static final String BASE_URL = "http://localhost:6565";

    public K6RestClient() {
        this.webClient = WebClient.builder()
                .baseUrl(BASE_URL)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public Mono<String> listMetrics() {
        return webClient.method(HttpMethod.GET)
                .uri(uriBuilder -> uriBuilder.path("/v1/metrics").build())
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(throwable -> log.error("Failed to list metrics", throwable));
    }
}
