package com.latte.worker.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@RequiredArgsConstructor
@Service
public class TestResultService {
    private static final Path SOURCE_PATH = Paths.get(System.getProperty("user.home"), ".latte", "workspace");
    private static final String LOG_FILE = "latte.log";

    public Mono<String> reportSummary() {
        return Mono.fromCallable(() -> Files.readString(SOURCE_PATH.resolve("summary.json")))
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<String> reportLog() {
        return Mono.fromCallable(() -> Files.readString(SOURCE_PATH.resolve(LOG_FILE)))
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Void> clean() {
        return Mono.fromRunnable(() -> deleteLog())
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }

    private void deleteLog() {
        try {
            Files.deleteIfExists(SOURCE_PATH.resolve(LOG_FILE));
        } catch (IOException e) {
            log.error("Failed to delete log", e);
            throw new RuntimeException();
        }
    }
}
