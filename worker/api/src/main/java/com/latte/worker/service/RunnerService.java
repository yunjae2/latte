package com.latte.worker.service;

import com.latte.worker.controller.request.RunnerRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RunnerService {
    private final TestSourceService testSourceService;
    private final TestExecutionService testExecutionService;

    public void run(RunnerRequest runnerRequest) {
        /* TODO: exception if already running */
        testSourceService.fetch(runnerRequest.getRepositoryUrl(), runnerRequest.getToken(), runnerRequest.getBranchName());
        testExecutionService.execute(runnerRequest.getScriptFilePath());
    }
}
