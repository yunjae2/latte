package com.latte.controller.client;

import com.latte.controller.config.ControllerConfig;
import com.latte.controller.config.IndentableServerSentEventHttpMessageReader;
import com.latte.controller.dto.RunConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Slf4j
@Component
@RefreshScope
public class WorkerClient {
    private final WebClient webClient;

    public WorkerClient(ControllerConfig controllerConfig) {
        webClient = WebClient.builder()
                .baseUrl(controllerConfig.getWorker().getUrl())
                .codecs(clientCodecConfigurer -> {
                    clientCodecConfigurer.registerDefaults(false);
                    clientCodecConfigurer.customCodecs().register(new IndentableServerSentEventHttpMessageReader());
                })
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public Flux<String> run(RunConfig runConfig) {
        return webClient.method(HttpMethod.GET)
                .uri(uriBuilder -> uriBuilder.path("/run")
                        .queryParam("repositoryUrl", runConfig.getRepositoryUrl())
                        .queryParam("token.name", runConfig.getToken().getName())
                        .queryParam("token.value", runConfig.getToken().getValue())
                        .queryParam("branchName", runConfig.getBranchName())
                        .queryParam("scriptFilePath", runConfig.getScriptFilePath())
                        .build())
                .retrieve()
                .bodyToFlux(String.class)
                .doOnError(throwable -> log.error("Failed to run a test on worker", throwable));
    }
}
