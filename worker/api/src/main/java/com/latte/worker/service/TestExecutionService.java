package com.latte.worker.service;

import com.latte.worker.dto.TestParameters;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@RequiredArgsConstructor
@Service
public class TestExecutionService {
    private static final Path SOURCE_PATH = Paths.get(System.getProperty("user.home"), ".latte", "workspace");
    private static final String LOG_FILE = "latte.log";

    public Flux<String> execute(String scriptFilePath) {
        String script = Paths.get(scriptFilePath).toString();

        return runAsync(SOURCE_PATH.toFile(), "k6", "run", "--console-output", LOG_FILE, "--no-color", script)
                .doOnNext(process -> log.info("Running the test.."))
                .flatMapMany(process -> DataBufferUtils.readInputStream(process::getInputStream, DefaultDataBufferFactory.sharedInstance, DefaultDataBufferFactory.DEFAULT_INITIAL_CAPACITY)
                        .doOnCancel(process::destroy))
                .map(this::convertToString)
                .doOnNext(log::info);
    }

    private String convertToString(DataBuffer dataBuffer) {
        ByteBuffer byteBuffer = dataBuffer.asByteBuffer();
        return StandardCharsets.UTF_8.decode(byteBuffer).toString();
    }

    public Mono<Void> applyParameters(TestParameters testParameters) {
        return Mono.fromRunnable(() -> applyParametersInternal(testParameters))
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }

    private void applyParametersInternal(TestParameters testParameters) {
        Long rate = testParameters.getTps();
        String duration = testParameters.getDuration();
        Long vus = testParameters.getTps() * testParameters.getEstimatedLatency() / 1000;
        Long maxVus = testParameters.getTps() * testParameters.getEstimatedPeakLatency() / 1000;

        try {
            run(SOURCE_PATH.toFile(), "../update_parameters.sh", rate.toString(), duration, vus.toString(), maxVus.toString())  // $ k6 run ${SCRIPT}
                    .waitFor();
        } catch (InterruptedException e) {
            log.error("Process interrupted", e);
            throw new IllegalStateException(e);
        }
    }

    private Mono<Process> runAsync(File workingDirectory, String... command) {
        return Mono.fromCallable(() -> run(workingDirectory, command))
                .subscribeOn(Schedulers.boundedElastic());
    }

    private Process run(File workingDirectory, String... command) {
        try {
            return new ProcessBuilder()
                    .directory(workingDirectory)
                    .command(command)
                    .redirectErrorStream(true)
                    .start();
        } catch (IOException e) {
            log.error("Failed to run the command {} at working directory {}", command, workingDirectory, e);
            throw new IllegalStateException(e);
        }
    }
}
