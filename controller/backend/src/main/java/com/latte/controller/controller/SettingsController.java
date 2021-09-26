package com.latte.controller.controller;

import com.latte.controller.controller.request.SettingsUpdateRequest;
import com.latte.controller.service.SettingsService;
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
@RequestMapping("/settings")
public class SettingsController {
    private final SettingsService settingsService;

    @PutMapping("/update")
    public Mono<Void> update(@Valid @RequestBody SettingsUpdateRequest settingsUpdateRequest) {
        return settingsService.update(settingsUpdateRequest.getControllerConfig())
                .doOnSuccess(v -> log.info("Settings updated successfully"));
    }
}
