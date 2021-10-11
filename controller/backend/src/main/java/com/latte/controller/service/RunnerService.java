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
    private final ResultRepository resultRepository;


    public Flux<String> run(RunnerRequest runnerRequest) {
        RunInfo runInfo = buildRunInfo(runnerRequest);
        RunConfig runConfig = buildConfig(runnerRequest);

        Flux<String> outputs = workerClient.run(runConfig).replay().autoConnect(2);

        Flux<String> result = outputs.skipLast(1);
        Mono<String> summary = outputs.last();

        saveSummary(runConfig, runInfo, summary);
        cacheResult(result);

        return result.doOnNext(log::info);
    }

    private void cacheResult(Flux<String> result) {
        result.take(1)  // Avoid caching failed run results */
                .doOnNext(v -> resultRepository.update(result))
                .subscribe();
    }


    private RunInfo buildRunInfo(RunnerRequest runnerRequest) {
        return RunInfo.builder()
                .startTime(LocalDateTime.now())
                .testName(runnerRequest.getTestName())
                .build();
    }

    private RunConfig buildConfig(RunnerRequest runnerRequest) {
        return RunConfig.builder()
                .repositoryUrl(runnerRequest.getRepositoryUrl())
                .username(controllerConfig.getSettings().getUsername())
                .password(controllerConfig.getSettings().getPassword())
                .branchName(runnerRequest.getBranchName())
                .scriptFilePath(runnerRequest.getScriptFilePath())
                .duration(runnerRequest.getDuration())
                .rps(runnerRequest.getRps())
                .estimatedLatency(runnerRequest.getEstimatedLatency())
                .estimatedPeakLatency(runnerRequest.getEstimatedPeakLatency())
                .build();
    }

    private void saveSummary(RunConfig runConfig, RunInfo runInfo, Mono<String> summary) {
        summary.flatMap(s -> historyService.save(runConfig, runInfo, s))
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe();
    }

    public Flux<String> replay() {
        return resultRepository.replay();
    }
}
