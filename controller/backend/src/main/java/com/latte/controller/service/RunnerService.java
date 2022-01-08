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

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@RequiredArgsConstructor
@Service
public class RunnerService {
    private final WorkerClient workerClient;
    private final ControllerConfig controllerConfig;
    private final HistoryService historyService;
    private final ResultRepository resultRepository;
    private volatile AtomicBoolean stop = new AtomicBoolean(false);

    private static final int REPLAY_SIZE = 180;

    public Flux<String> run(RunnerRequest runnerRequest) {
        RunInfo runInfo = buildRunInfo(runnerRequest);
        RunConfig runConfig = buildConfig(runnerRequest);

        Flux<String> outputs = workerClient.run(runConfig)
                .takeUntil(output -> stop.getAndSet(false))
                .replay(REPLAY_SIZE)
                .autoConnect();

        Flux<String> result = outputs.skipLast(2);
        Mono<String> summary = outputs.takeLast(2).elementAt(0);
        Mono<String> consoleLog = outputs.takeLast(2).elementAt(1);

        cacheResult(result);

        return result.doOnNext(log::info)
                .concatWith(saveHistory(runConfig, runInfo, summary, consoleLog)
                        .cast(String.class));
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
                .tps(runnerRequest.getTps())
                .estimatedLatency(runnerRequest.getEstimatedLatency())
                .estimatedPeakLatency(runnerRequest.getEstimatedPeakLatency())
                .build();
    }

    private Mono<Void> saveHistory(RunConfig runConfig, RunInfo runInfo, Mono<String> summary, Mono<String> consoleLog) {
        return Mono.zip(summary, consoleLog)
                .filter(tuple -> tuple.getT1().startsWith("{") && tuple.getT1().endsWith("}"))
                .flatMap(tuple -> historyService.save(runConfig, runInfo, tuple.getT1(), tuple.getT2()));
    }

    public Flux<String> replay() {
        return resultRepository.replay();
    }

    public Mono<Boolean> stop() {
        this.stop.set(true);
        return Mono.just(true);
    }

    public Mono<Boolean> reset() {
        return Mono.fromRunnable(resultRepository::initialize)
                .then(stop())
                .doOnSuccess(v -> log.info("Reset complete."));
    }
}
