package com.latte.controller.service;

import com.latte.controller.client.WorkerClient;
import com.latte.controller.controller.request.RunnerRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

@Slf4j
@RequiredArgsConstructor
@Service
public class RunnerService {
    private final WorkerClient workerClient;

    public void run(RunnerRequest runnerRequest) {
        /* TODO: return to frontend */
        workerClient.run(runnerRequest)
                .subscribe(dataBuffer -> {
                    ByteBuffer byteBuffer = dataBuffer.asByteBuffer();
                    log.info(StandardCharsets.UTF_8.decode(byteBuffer).toString());
                });
    }
}
