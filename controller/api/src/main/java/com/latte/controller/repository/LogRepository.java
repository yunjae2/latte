package com.latte.controller.repository;

import com.latte.controller.property.LogProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Slf4j
@Repository
@RequiredArgsConstructor
public class LogRepository {
    private final LogProperties logProperties;

    public Resource getLog(String logPath) {
        return new FileSystemResource(logProperties.getPath().resolve(logPath));
    }

    public String save(String consoleLog) {
        prepareLogDirectory();

        Path logPath;

        while (true) {
            String logFileName = "latte-" + UUID.randomUUID() + ".log";
            logPath = logProperties.getPath().resolve(logFileName);
            try {
                if (logPath.toFile().createNewFile()) {
                    break;
                }
            } catch (IOException e) {
                log.error("Failed to create log file");
                throw new RuntimeException(e);
            }
        }

        try {
            Files.write(logPath, consoleLog.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            log.error("Failed to write log to file");
            throw new RuntimeException(e);
        }

        return logPath.getFileName().toString();
    }

    private void prepareLogDirectory() {
        if (logProperties.getPath().toFile().exists())
            return;

        logProperties.getPath().toFile().mkdirs();
    }
}
