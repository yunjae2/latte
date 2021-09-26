package com.latte.worker.controller;

import com.latte.worker.controller.request.RegisterRequest;
import com.latte.worker.service.RegisterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/register")
public class RegisterController {
    private final RegisterService registerService;

    @PutMapping
    public Mono<Void> register(@Valid @RequestBody RegisterRequest registerRequest) {
        return registerService.register(registerRequest.getWorkerConfig())
                .doOnSuccess(v -> log.info("Worker registered successfully"));
    }
}
