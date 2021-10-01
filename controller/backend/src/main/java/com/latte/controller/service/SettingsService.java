package com.latte.controller.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.latte.controller.config.ControllerConfig;
import com.latte.controller.config.ControllerConfig.Git;
import com.latte.controller.config.ControllerConfig.Worker;
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

    public Mono<ControllerConfig> get() {
        return Mono.just(controllerConfig.toPublicConfig());
    }

    public Mono<Void> update(ControllerConfig newConfig) {
        return Mono.fromRunnable(() -> updateSettings(newConfig));
    }

    public Mono<Void> updateWorker(Worker worker) {
        ControllerConfig newConfig = ControllerConfig.builder()
                .worker(worker)
                .git(controllerConfig.getGit())
                .build();

        return Mono.fromRunnable(() -> updateSettings(newConfig));
    }

    public Mono<Void> updateGit(Git git) {
        ControllerConfig newConfig = ControllerConfig.builder()
                .worker(controllerConfig.getWorker())
                .git(git)
                .build();

        return Mono.fromRunnable(() -> updateSettings(newConfig));
    }

    private void updateSettings(ControllerConfig controllerConfig) {
        writeSettings(controllerConfig);
        refreshSettings();
    }

    private void writeSettings(ControllerConfig controllerConfig) {
        try {
            mapper.writeValue(SETTINGS_PATH.toFile(), controllerConfig);
        } catch (IOException e) {
            log.error("Failed to write settings", e);
            throw new IllegalStateException(e);
        }
    }

    private void refreshSettings() {
        refreshEndpoint.refresh();
    }
}
