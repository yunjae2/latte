package com.latte.controller.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.latte.controller.config.ControllerConfig;
import com.latte.controller.config.ControllerConfig.Settings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.endpoint.RefreshEndpoint;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.fasterxml.jackson.databind.SerializationFeature.WRAP_ROOT_VALUE;
import static com.fasterxml.jackson.dataformat.yaml.YAMLGenerator.Feature.MINIMIZE_QUOTES;
import static com.fasterxml.jackson.dataformat.yaml.YAMLGenerator.Feature.WRITE_DOC_START_MARKER;

@RequiredArgsConstructor
@Slf4j
@Service
public class SettingsService {
    private static final ObjectMapper mapper = new ObjectMapper(new YAMLFactory().disable(WRITE_DOC_START_MARKER).enable(MINIMIZE_QUOTES))
            .configure(WRAP_ROOT_VALUE, true);
    private static final Path SETTINGS_PATH = Paths.get(System.getProperty("user.home"), ".latte", "controller.yml");

    private final RefreshEndpoint refreshEndpoint;
    private final ControllerConfig controllerConfig;

    public Mono<Settings> get() {
        return Mono.fromSupplier(() -> controllerConfig.getSettings().toPublicSettings());
    }

    public Mono<Void> update(Settings settings) {
        return applySettings(settings);
    }

    public Mono<Void> updateWorker(String workerUrl) {
        return applySettings(Settings.builder()
                .workerUrl(workerUrl)
                .username(controllerConfig.getSettings().getUsername())
                .password(controllerConfig.getSettings().getPassword())
                .build());
    }

    public Mono<Void> updateAuth(String username, String password) {
        return applySettings(Settings.builder()
                .workerUrl(controllerConfig.getSettings().getWorkerUrl())
                .username(username)
                .password(password)
                .build());
    }

    private Mono<Void> applySettings(Settings settings) {
        ControllerConfig newConfig = ControllerConfig.builder()
                .registered(true)
                .settings(settings)
                .build();

        return Mono.fromRunnable(() -> applyConfig(newConfig));
    }

    private void applyConfig(ControllerConfig controllerConfig) {
        writeConfig(controllerConfig);
        refreshConfig();
    }

    private void writeConfig(ControllerConfig controllerConfig) {
        try {
            mapper.writeValue(SETTINGS_PATH.toFile(), controllerConfig);
        } catch (IOException e) {
            log.error("Failed to write settings", e);
            throw new IllegalStateException(e);
        }
    }

    private void refreshConfig() {
        refreshEndpoint.refresh();
    }
}
