package com.latte.controller.service;

import com.latte.controller.dto.FileInfo;
import com.latte.controller.repository.ScriptRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ScriptService {
    private final ScriptRepository scriptRepository;

    public Mono<List<FileInfo>> findAll() {
        return Mono.fromCallable(() -> scriptRepository.findAll())
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<String> readFile(String fileName) {
        return Mono.fromCallable(() -> scriptRepository.readFile(fileName))
                .subscribeOn(Schedulers.boundedElastic());
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
