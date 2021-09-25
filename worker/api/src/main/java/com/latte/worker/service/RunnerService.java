package com.latte.worker.service;

import com.latte.worker.controller.request.RunnerRequest;
import com.latte.worker.dto.SourceConfig;
import com.latte.worker.dto.TestConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class RunnerService {
    private final TestSourceService testSourceService;
    private final TestExecutionService testExecutionService;
    private final TestSummaryService testSummaryService;

    public Flux<DataBuffer> run(RunnerRequest runnerRequest) {
        /* TODO: exception if already running */
        SourceConfig sourceConfig = SourceConfig.builder()
                .repositoryUrl(runnerRequest.getRepositoryUrl())
                .branchName(runnerRequest.getBranchName())
                .token(runnerRequest.getToken())
                .build();

        TestConfig testConfig = TestConfig.builder()
                .testName(runnerRequest.getTestName())
                .startTime(LocalDateTime.now())
                .branchName(runnerRequest.getBranchName())
                .scriptFilePath(runnerRequest.getScriptFilePath())
                .build();

        testSourceService.fetch(sourceConfig);
        return testExecutionService.execute(runnerRequest.getScriptFilePath())
                .doOnComplete(() -> testSummaryService.report(testConfig));
    }
}
