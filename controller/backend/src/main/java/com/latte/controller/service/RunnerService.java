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

    public Flux<String> run(RunnerRequest runnerRequest) {
        RunInfo runInfo = buildRunInfo(runnerRequest);
        RunConfig runConfig = buildConfig(runnerRequest);

        Flux<String> outputs = workerClient.run(runConfig)
                .takeUntil(output -> stop.getAndSet(false))
                .replay()
                .autoConnect();

        Flux<String> result = outputs.skipLast(1);
        Mono<String> summary = outputs.last();

        cacheResult(result);

        return result.doOnNext(log::info)
                .concatWith(saveSummary(runConfig, runInfo, summary)
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
                .rps(runnerRequest.getRps())
                .estimatedLatency(runnerRequest.getEstimatedLatency())
                .estimatedPeakLatency(runnerRequest.getEstimatedPeakLatency())
                .build();
    }

    private Mono<Void> saveSummary(RunConfig runConfig, RunInfo runInfo, Mono<String> summary) {
        return summary
                .filter(s -> s.startsWith("{") && s.endsWith("}"))
                .flatMap(s -> historyService.save(runConfig, runInfo, s));
    }

    public Flux<String> replay() {
        return resultRepository.replay();
    }

    public Mono<Boolean> stop() {
        this.stop.set(true);
        return Mono.just(true);
    }
}
