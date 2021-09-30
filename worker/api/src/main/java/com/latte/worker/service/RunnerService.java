package com.latte.worker.service;

import com.latte.worker.controller.request.RunnerRequest;
import com.latte.worker.dto.SourceConfig;
import com.latte.worker.dto.TestParameters;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.concurrent.Semaphore;

@Slf4j
@RequiredArgsConstructor
@Service
public class RunnerService {
    private final TestSourceService testSourceService;
    private final TestExecutionService testExecutionService;
    private final TestSummaryService testSummaryService;
    private final Semaphore runnerLock = new Semaphore(1);

    public Flux<String> tryRun(RunnerRequest runnerRequest) {
        if (runnerLock.tryAcquire()) {
            return run(runnerRequest)
                    .doOnTerminate(runnerLock::release);
        } else {
            log.error("Test already running");
            throw new IllegalStateException();
        }
    }

    public Flux<String> run(RunnerRequest runnerRequest) {
        SourceConfig sourceConfig = SourceConfig.builder()
                .repositoryUrl(runnerRequest.getRepositoryUrl())
                .branchName(runnerRequest.getBranchName())
                .token(runnerRequest.getToken())
                .build();

        TestParameters testParameters = TestParameters.builder()
                .duration(runnerRequest.getDuration())
                .rps(runnerRequest.getRps())
                .estimatedLatency(runnerRequest.getEstimatedLatency())
                .estimatedPeakLatency(runnerRequest.getEstimatedPeakLatency())
                .build();

        testSourceService.fetch(sourceConfig);
        testExecutionService.applyParameters(testParameters);
        return Flux.concat(testExecutionService.execute(runnerRequest.getScriptFilePath()),
                        testSummaryService.report());
    }
}
