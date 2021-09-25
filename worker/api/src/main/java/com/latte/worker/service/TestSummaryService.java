package com.latte.worker.service;

import com.latte.worker.client.ControllerClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.scheduler.Schedulers;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@RequiredArgsConstructor
@Service
public class TestSummaryService {
    private static final Path SOURCE_PATH = Paths.get(System.getProperty("user.home"), ".latte", "workspace");

    private final ControllerClient controllerClient;

    public void report() {
        File summary = SOURCE_PATH.resolve("results").resolve("summary.json").toFile();
        controllerClient.reportSummary(summary)
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe();
    }
}
