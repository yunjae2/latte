package com.latte.worker.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.File;

@Slf4j
@Component
public class ControllerClient {
    private final WebClient webClient;

    public ControllerClient() {
        /* TODO: Use configuration for url */
        webClient = WebClient.builder()
                .baseUrl("http://localhost:8080")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE)
                .build();
    }

    public Mono<Void> reportSummary(File summary) {
        return webClient.method(HttpMethod.POST)
                .uri("/history")
                .body(BodyInserters.fromMultipartData(fromFile("summary", summary)))
                .retrieve()
                .bodyToMono(Void.class)
                .doOnError(throwable -> log.error("Failed to report the summary", throwable));
    }

    private MultiValueMap<String, HttpEntity<?>> fromFile(String name, File file) {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part(name, new FileSystemResource(file));
        return builder.build();
    }

}
