package com.latte.worker.service;

import com.latte.worker.client.ControllerClient;
import com.latte.worker.dto.TestConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@RequiredArgsConstructor
@Service
public class TestSummaryService {
    private static final Path SOURCE_PATH = Paths.get(System.getProperty("user.home"), ".latte", "workspace");

    private final ControllerClient controllerClient;

    public void report(TestConfig testConfig) {
        String summary;
        try {
            summary = Files.readString(SOURCE_PATH.resolve("results").resolve("summary.json"));
        } catch (IOException e) {
            log.error("Failed to read summary file", e);
            throw new IllegalStateException(e);
        }
        controllerClient.reportSummary(testConfig.getTestName(), testConfig.getStartTime(), testConfig.getBranchName(), testConfig.getScriptFilePath(), summary)
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe();
    }
}
