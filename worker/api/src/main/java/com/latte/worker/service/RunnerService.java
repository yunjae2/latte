package com.latte.worker.service;

import com.latte.worker.controller.request.RunnerRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
@Service
public class RunnerService {
    private final TestSourceService testSourceService;
    private final TestExecutionService testExecutionService;
    private final TestSummaryService testSummaryService;

    public Flux<DataBuffer> run(RunnerRequest runnerRequest) {
        /* TODO: exception if already running */
        testSourceService.fetch(runnerRequest.getRepositoryUrl(), runnerRequest.getToken(), runnerRequest.getBranchName());
        return testExecutionService.execute(runnerRequest.getScriptFilePath())
                .doOnComplete(testSummaryService::report);
    }
}
