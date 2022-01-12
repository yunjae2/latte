package com.latte.worker.service;

import com.latte.worker.client.K6RestClient;
import com.latte.worker.dto.TestParameters;
import com.latte.worker.repository.TestProcessRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;

@Slf4j
@RequiredArgsConstructor
@Service
public class TestExecutionService {
    private final K6RestClient k6RestClient;
    private final TestProcessRepository testProcessRepository;

    private static final Path SOURCE_PATH = Paths.get(System.getProperty("user.home"), ".latte", "workspace");
    private static final String LOG_FILE = "latte.log";

    public Flux<String> execute(String scriptFilePath) {
        String script = Paths.get(scriptFilePath).toString();

        return runAsync(SOURCE_PATH.toFile(), "k6", "run", "--no-color", script)
                .doOnNext(process -> log.info("Running the test.."))
                .flatMapMany(this::streamResults)
                .doOnNext(result -> log.info("Still running..."))
                .doOnComplete(() -> log.info("Test finished"));
    }

    private Flux<String> streamResults(Process process) {
        return Flux.interval(Duration.ofSeconds(1))
                .takeWhile(seq -> process.isAlive())
                .flatMapSequential(this::getRuntimeStats);
    }

    private Mono<String> getRuntimeStats(Long seq) {
        return k6RestClient.listMetrics()
                .map(metricBody -> "{\n\"time\": " + "" + seq + ".0,\n" + "\"stat\": " + metricBody + "\n}")
                .onErrorResume(throwable -> Mono.empty());
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
                .doOnNext(testProcessRepository::update)
                .doOnCancel(testProcessRepository::delete)
                .subscribeOn(Schedulers.boundedElastic());
    }

    private Process run(File workingDirectory, String... command) {
        try {
            return new ProcessBuilder()
                    .directory(workingDirectory)
                    .command(command)
                    .redirectErrorStream(true)
                    .redirectOutput(SOURCE_PATH.resolve(LOG_FILE).toFile())
                    .start();
        } catch (IOException e) {
            log.error("Failed to run the command {} at working directory {}", command, workingDirectory, e);
            throw new IllegalStateException(e);
        }
    }
}
