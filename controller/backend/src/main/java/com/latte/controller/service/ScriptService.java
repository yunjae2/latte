package com.latte.controller.service;

import com.latte.controller.dto.FileInfo;
import com.latte.controller.repository.ScriptRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ScriptService {
    private static final Path SCRIPT_ROOT = Paths.get("/var/www/git/repository");
    private final ScriptRepository scriptRepository;

    public Mono<List<FileInfo>> findAll() {
        return Mono.fromCallable(() -> findAllInternal())
                .subscribeOn(Schedulers.boundedElastic());
    }

    private List<FileInfo> findAllInternal() {
        try {
            return Files.walk(SCRIPT_ROOT)
                    .filter(Files::isRegularFile)
                    .filter(path -> !SCRIPT_ROOT.relativize(path).startsWith(".git"))
                    .map(path -> FileInfo.fromPath(SCRIPT_ROOT, path))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.error("Failed during building file tree", e);
            throw new IllegalStateException(e);
        }
    }

    public Mono<String> readFile(String fileName) {
        return Mono.fromCallable(() -> getStringInternal(fileName))
                .subscribeOn(Schedulers.boundedElastic());
    }

    private String getStringInternal(String fileName) {
        try {
            return Files.readString(SCRIPT_ROOT.resolve(fileName));
        } catch (IOException e) {
            log.error("Failed to read file {}", fileName, e);
            throw new IllegalStateException(e);
        }
    }

    public Mono<Boolean> commit(String fileName, String content, String message) {
        return Mono.fromRunnable(() -> commitInternal(fileName, content, message))
                .subscribeOn(Schedulers.boundedElastic())
                .then(Mono.just(true))
                .doOnSuccess(v -> log.info("New commit: {}", message));
    }

    private void commitInternal(String fileName, String content, String message) {
        scriptRepository.write(fileName, content)
                .add(fileName)
                .commit(message);
    }

    public Mono<List<String>> getBranches() {
        return Mono.fromCallable(() -> scriptRepository.getBranches())
                .subscribeOn(Schedulers.boundedElastic())
                .doOnSuccess(branches -> log.info("Branches: " + branches));
    }
}
