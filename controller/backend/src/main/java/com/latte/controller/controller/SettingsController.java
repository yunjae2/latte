package com.latte.controller.controller;

import com.latte.controller.controller.request.AuthUpdateRequest;
import com.latte.controller.controller.request.SettingsUpdateRequest;
import com.latte.controller.controller.request.WorkerUpdateRequest;
import com.latte.controller.controller.response.SettingsGetResponse;
import com.latte.controller.service.SettingsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/settings")
public class SettingsController {
    private final SettingsService settingsService;

    @GetMapping
    public Mono<SettingsGetResponse> get() {
        return settingsService.get()
                .map(SettingsGetResponse::from);
    }

    @PutMapping("/update")
    public Mono<SettingsGetResponse> update(@Valid @RequestBody SettingsUpdateRequest settingsUpdateRequest) {
        return settingsService.update(settingsUpdateRequest.getSettings())
                .then(settingsService.get())
                .map(SettingsGetResponse::from)
                .doOnSuccess(v -> log.info("Settings updated successfully"));
    }

    @PutMapping("/update/worker")
    public Mono<SettingsGetResponse> update(@Valid @RequestBody WorkerUpdateRequest workerUpdateRequest) {
        return settingsService.updateWorker(workerUpdateRequest.getWorkerUrl())
                .then(settingsService.get())
                .map(SettingsGetResponse::from)
                .doOnSuccess(v -> log.info("Worker settings updated successfully"));
    }

    @PutMapping("/update/auth")
    public Mono<SettingsGetResponse> update(@Valid @RequestBody AuthUpdateRequest authUpdateRequest) {
        return settingsService.updateAuth(authUpdateRequest.getUsername(), authUpdateRequest.getPassword())
                .then(settingsService.get())
                .map(SettingsGetResponse::from)
                .doOnSuccess(v -> log.info("Auth settings updated successfully"));
    }
}
