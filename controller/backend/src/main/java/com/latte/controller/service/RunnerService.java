package com.latte.controller.service;

import com.latte.controller.client.WorkerClient;
import com.latte.controller.config.ControllerConfig;
import com.latte.controller.controller.request.RunnerRequest;
import com.latte.controller.dto.RunConfig;
import com.latte.controller.dto.RunInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Service
public class RunnerService {
    private final WorkerClient workerClient;
    private final ControllerConfig controllerConfig;
    private final HistoryService historyService;

    public Flux<String> run(RunnerRequest runnerRequest) {
        RunInfo runInfo = buildRunInfo(runnerRequest);
        RunConfig runConfig = buildConfig(runnerRequest);
        Flux<String> outputs = workerClient.run(runConfig).publish().autoConnect(2);
        saveSummary(runConfig, runInfo, outputs.last());
        return outputs.skipLast(1)
                .doOnNext(log::info);
    }

    private RunInfo buildRunInfo(RunnerRequest runnerRequest) {
        return RunInfo.builder()
                .startTime(LocalDateTime.now())
                .testName(runnerRequest.getTestName())
                .build();
    }

    private RunConfig buildConfig(RunnerRequest runnerRequest) {
        return RunConfig.builder()
                .repositoryUrl(controllerConfig.getGit().getUrl())
                .token(controllerConfig.getGit().getToken())
                .branchName(runnerRequest.getBranchName())
                .scriptFilePath(runnerRequest.getScriptFilePath())
                .build();
    }

    private void saveSummary(RunConfig runConfig, RunInfo runInfo, Mono<String> summary) {
        summary.flatMap(s -> historyService.save(runConfig, runInfo, s))
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe();
    }
}
