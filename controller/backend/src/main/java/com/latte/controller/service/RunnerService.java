package com.latte.controller.service;

import com.latte.controller.client.WorkerClient;
import com.latte.controller.config.ControllerConfig;
import com.latte.controller.controller.request.RunnerRequest;
import com.latte.controller.dto.RunConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

@Slf4j
@RequiredArgsConstructor
@Service
public class RunnerService {
    private final WorkerClient workerClient;
    private final ControllerConfig controllerConfig;

    public Flux<String> run(RunnerRequest runnerRequest) {

        RunConfig runConfig = buildConfig(runnerRequest);
        return workerClient.run(runConfig)
                .filter(output -> {
                    log.info(output);
                    return true;
                })
                .doOnComplete(() -> {
                    /* TODO: convert to history entity */
                    /* TODO: save the entity */
                });
    }

    private RunConfig buildConfig(RunnerRequest runnerRequest) {
        return RunConfig.builder()
                .repositoryUrl(controllerConfig.getGit().getUrl())
                .token(controllerConfig.getGit().getToken())
                .branchName(runnerRequest.getBranchName())
                .scriptFilePath(runnerRequest.getScriptFilePath())
                .build();
    }
}
