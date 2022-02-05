package com.latte.controller.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.latte.controller.property.ControllerProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.endpoint.RefreshEndpoint;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.fasterxml.jackson.databind.SerializationFeature.WRAP_ROOT_VALUE;
import static com.fasterxml.jackson.dataformat.yaml.YAMLGenerator.Feature.MINIMIZE_QUOTES;
import static com.fasterxml.jackson.dataformat.yaml.YAMLGenerator.Feature.WRITE_DOC_START_MARKER;

@Slf4j
@RequiredArgsConstructor
@Repository
public class ConfigRepository {
    private static final ObjectMapper mapper = new ObjectMapper(new YAMLFactory().disable(WRITE_DOC_START_MARKER).enable(MINIMIZE_QUOTES))
            .configure(WRAP_ROOT_VALUE, true);
    private static final Path SETTINGS_PATH = Paths.get(System.getProperty("user.home"), ".latte", "controller.yml");

    private final RefreshEndpoint refreshEndpoint;

    public void applyConfig(ControllerProperties controllerConfig) {
        writeConfig(controllerConfig);
        refreshConfig();
    }

    private void writeConfig(ControllerProperties controllerConfig) {
        try {
            mapper.writeValue(SETTINGS_PATH.toFile(), controllerConfig);
        } catch (IOException e) {
            log.error("Failed to write configs", e);
            throw new IllegalStateException(e);
        }
    }

    private void refreshConfig() {
        refreshEndpoint.refresh();
    }

}
