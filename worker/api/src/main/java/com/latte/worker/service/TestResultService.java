package com.latte.worker.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@RequiredArgsConstructor
@Service
public class TestResultService {
    private static final Path SOURCE_PATH = Paths.get(System.getProperty("user.home"), ".latte", "workspace");
    private static final String LOG_FILE = "latte.log";
    private static final long MAX_LOG_SIZE = 1024 * 1024L;       // 1 MiB

    public Mono<String> reportSummary() {
        return Mono.fromCallable(() -> Files.readString(SOURCE_PATH.resolve("summary.json")))
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<String> reportLog() {
        return Mono.fromCallable(this::readLog)
                .subscribeOn(Schedulers.boundedElastic());
    }

    private String readLog() {
        Path logFilePath = SOURCE_PATH.resolve(LOG_FILE);

        try (InputStream inputStream = Files.newInputStream(logFilePath)) {
            long actualSize = Files.size(logFilePath);
            long startPosition = 0;
            if (actualSize > MAX_LOG_SIZE) {
                startPosition = actualSize - MAX_LOG_SIZE;
            }
            inputStream.skip(startPosition);

            log.info("Reading log file from pos {} with size {}", startPosition, actualSize - startPosition);
            byte[] rawLog = inputStream.readAllBytes();

            return new String(rawLog, StandardCharsets.UTF_8);

        } catch (FileNotFoundException e) {
            log.error("Log file not found");
            throw new RuntimeException(e);

        } catch (IOException e) {
            log.error("Failed to read log file");
            throw new RuntimeException(e);
        }
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
