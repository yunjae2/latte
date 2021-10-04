package com.latte.controller.service;

import com.latte.controller.dto.FileInfo;
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
@Service
public class ScriptService {
    private static final Path SCRIPT_ROOT = Paths.get("/var/www/git/repository");

    public Mono<List<FileInfo>> findAll() {
        return Mono.fromCallable(() -> findAllInternal())
                .subscribeOn(Schedulers.boundedElastic());
    }

    private List<FileInfo> findAllInternal() {
        try {
            return Files.walk(SCRIPT_ROOT)
                    .filter(Files::isRegularFile)
                    .map(path -> FileInfo.fromPath(SCRIPT_ROOT, path))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.error("Failed during building file tree", e);
            throw new IllegalStateException(e);
        }
    }
}
