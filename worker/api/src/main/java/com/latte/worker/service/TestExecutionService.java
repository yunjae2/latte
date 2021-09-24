package com.latte.worker.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@RequiredArgsConstructor
@Service
public class TestExecutionService {
    private static final Path SOURCE_PATH = Paths.get(System.getProperty("user.home"), ".latte", "workspace");

    public Flux<DataBuffer> execute(String scriptFilePath) {
        String executable = SOURCE_PATH.resolve("run.sh").toAbsolutePath().toString();
        String argument = SOURCE_PATH.resolve(scriptFilePath).toAbsolutePath().toString();

        Process process;
        try {
            process = new ProcessBuilder()
                    .command(executable, argument)
                    .redirectErrorStream(true)
                    .start();
        } catch (IOException e) {
            log.error("Failed to run the test", e);
            throw new IllegalStateException(e);
        }

        log.info("Running the test..");
        Flux<DataBuffer> output = DataBufferUtils.readInputStream(process::getInputStream, DefaultDataBufferFactory.sharedInstance, DefaultDataBufferFactory.DEFAULT_INITIAL_CAPACITY);
        return output.doOnNext(dataBuffer -> {
                    ByteBuffer byteBuffer = dataBuffer.asByteBuffer();
                    log.info(StandardCharsets.UTF_8.decode(byteBuffer).toString());
                });
    }
}
