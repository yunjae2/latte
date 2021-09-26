package com.latte.worker.service;

import com.latte.worker.controller.request.RunnerRequest;
import com.latte.worker.dto.SourceConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
@Service
public class RunnerService {
    private final TestSourceService testSourceService;
    private final TestExecutionService testExecutionService;
    private final TestSummaryService testSummaryService;

    public Flux<String> run(RunnerRequest runnerRequest) {
        /* TODO: exception if already running */
        SourceConfig sourceConfig = SourceConfig.builder()
                .repositoryUrl(runnerRequest.getRepositoryUrl())
                .branchName(runnerRequest.getBranchName())
                .token(runnerRequest.getToken())
                .build();

        testSourceService.fetch(sourceConfig);
        return Flux.concat(testExecutionService.execute(runnerRequest.getScriptFilePath()),
                        testSummaryService.report());
    }
}
