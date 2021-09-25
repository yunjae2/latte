package com.latte.controller.client;

import com.latte.controller.dto.RunConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Slf4j
@Component
public class WorkerClient {
    private final WebClient webClient;

    public WorkerClient() {
        /* TODO: Use configuration for url */
        webClient = WebClient.builder()
                .baseUrl("http://localhost:8081")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public Flux<DataBuffer> run(RunConfig runConfig) {
        return webClient.method(HttpMethod.POST)
                .uri("/run")
                .bodyValue(runConfig)
                .retrieve()
                .bodyToFlux(DataBuffer.class)
                .doOnError(throwable -> log.error("Failed to call worker", throwable));
    }
}
