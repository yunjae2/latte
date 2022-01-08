package com.latte.worker.service;

import com.latte.worker.controller.request.RunnerRequest;
import com.latte.worker.dto.SourceConfig;
import com.latte.worker.dto.TestParameters;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@RequiredArgsConstructor
@Service
public class RunnerService {
    private final TestSourceService testSourceService;
    private final TestExecutionService testExecutionService;
    private final TestResultService testResultService;
    private final AtomicBoolean running = new AtomicBoolean(false);

    public Flux<String> tryRun(RunnerRequest runnerRequest) {
        if (!running.getAndSet(true)) {
            AtomicBoolean released = new AtomicBoolean(false);
            return run(runnerRequest)
                    .doOnCancel(() -> release(released))
                    .doOnTerminate(() -> release(released));
        } else {
            log.error("Test already running");
            throw new IllegalStateException();
        }
    }

    private void release(AtomicBoolean released) {
        if (!released.getAndSet(true)) {
            running.set(false);
        }
    }

    public Flux<String> run(RunnerRequest runnerRequest) {
        SourceConfig sourceConfig = SourceConfig.builder()
                .repositoryUrl(runnerRequest.getRepositoryUrl())
                .branchName(runnerRequest.getBranchName())
                .username(runnerRequest.getUsername())
                .password(runnerRequest.getPassword())
                .build();

        TestParameters testParameters = TestParameters.builder()
                .duration(runnerRequest.getDuration())
                .tps(runnerRequest.getTps())
                .estimatedLatency(runnerRequest.getEstimatedLatency())
                .estimatedPeakLatency(runnerRequest.getEstimatedPeakLatency())
                .build();

        return testSourceService.fetch(sourceConfig)
                .then(testExecutionService.applyParameters(testParameters))
                .then(testResultService.clean())
                .thenMany(Flux.concat(
                        testExecutionService.execute(runnerRequest.getScriptFilePath()),
                        testResultService.reportSummary(),
                        testResultService.reportLog()))
                .publishOn(Schedulers.single());
    }
}
