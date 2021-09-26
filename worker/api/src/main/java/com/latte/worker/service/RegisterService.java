package com.latte.worker.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.latte.worker.config.WorkerConfig;
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

@Slf4j
@RequiredArgsConstructor
@Service
public class RegisterService {
    private static final ObjectMapper mapper = new ObjectMapper(new YAMLFactory().disable(WRITE_DOC_START_MARKER).enable(MINIMIZE_QUOTES))
            .configure(WRAP_ROOT_VALUE, true);
    private static final Path SETTINGS_PATH = Paths.get(System.getProperty("user.home"), ".latte", "worker.yml");

    private final RefreshEndpoint refreshEndpoint;

    public Mono<Void> register(WorkerConfig workerConfig) {
        return Mono.fromRunnable(() -> registerSettings(workerConfig));
    }

    private void registerSettings(WorkerConfig workerConfig) {
        writeSettings(workerConfig);
        refreshSettings();
    }

    private void writeSettings(WorkerConfig workerConfig) {
        try {
            mapper.writeValue(SETTINGS_PATH.toFile(), workerConfig);
        } catch (IOException e) {
            log.error("Failed to write settings", e);
            throw new IllegalStateException(e);
        }
    }

    private void refreshSettings() {
        refreshEndpoint.refresh();
    }

}
