package com.latte.controller.service;

import com.latte.controller.property.ControllerProperties;
import com.latte.controller.property.ControllerProperties.Settings;
import com.latte.controller.repository.ConfigRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/* TODO: prohibit config update when not registered */
@RequiredArgsConstructor
@Slf4j
@Service
public class SettingsService {
    private final ControllerProperties controllerProperties;
    private final UserService userService;
    private final ConfigRepository configRepository;

    public Mono<Settings> get() {
        return Mono.fromSupplier(() -> controllerProperties.getSettings().toPublicSettings());
    }

    public Mono<Void> update(Settings settings) {
        return applySettings(settings);
    }

    public Mono<Void> updateWorker(String workerUrl) {
        return applySettings(Settings.builder()
                .workerUrl(workerUrl)
                .username(controllerProperties.getSettings().getUsername())
                .password(controllerProperties.getSettings().getPassword())
                .build());
    }

    public Mono<Void> updateAuth(String username, String password) {
        return userService.register(username, password)
                .then(applySettings(Settings.builder()
                        .workerUrl(controllerProperties.getSettings().getWorkerUrl())
                        .username(username)
                        .password(password)
                        .build()));
    }

    private Mono<Void> applySettings(Settings settings) {
        ControllerProperties newConfig = ControllerProperties.builder()
                .registered(true)
                .settings(settings)
                .build();

        return Mono.fromRunnable(() -> configRepository.applyConfig(newConfig));
    }

    public Mono<Boolean> register(Settings settings) {
        if (controllerProperties.getRegistered()) {
            log.error("Already registered");
            throw new IllegalStateException();
        }

        return userService.register(settings.getUsername(), settings.getPassword())
                .then(applySettings(settings))
                .then(Mono.just(true));
    }

    public Mono<Boolean> checkRegistered() {
        return Mono.just(controllerProperties.getRegistered());
    }
}
