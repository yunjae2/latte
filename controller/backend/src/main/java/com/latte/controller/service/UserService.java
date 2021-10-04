package com.latte.controller.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {
    private static final Path LATTE_HOME = Paths.get(System.getProperty("user.home"), ".latte");

    public Mono<Void> register(String username, String password) {
        return Mono.fromRunnable(() -> registerInternal(username, password))
                .then()
                .subscribeOn(Schedulers.boundedElastic());
    }

    private void registerInternal(String username, String password) {
        try {
            run(LATTE_HOME.toFile(), "./register_user.sh", username, password)
                    .waitFor();
        } catch (InterruptedException e) {
            log.error("Failed to register user {}", username, e);
            throw new IllegalStateException(e);
        }
    }

    private Process run(File workingDirectory, String... command) {
        try {
            return new ProcessBuilder()
                    .directory(workingDirectory)
                    .command(command)
                    .redirectErrorStream(true)
                    .start();
        } catch (IOException e) {
            log.error("Failed to run the command {} at working directory {}", command, workingDirectory, e);
            throw new IllegalStateException(e);
        }
    }
}