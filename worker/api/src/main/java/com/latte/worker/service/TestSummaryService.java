package com.latte.worker.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@RequiredArgsConstructor
@Service
public class TestSummaryService {
    private static final Path SOURCE_PATH = Paths.get(System.getProperty("user.home"), ".latte", "workspace");

    public Mono<String> report() {
        return Mono.fromCallable(() -> Files.readString(SOURCE_PATH.resolve("results").resolve("summary.json")))
                .subscribeOn(Schedulers.boundedElastic());
    }
}
