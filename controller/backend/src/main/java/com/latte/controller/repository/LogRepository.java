package com.latte.controller.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@Repository
public class LogRepository {
    private static final Path LOG_ROOT_DIR = Paths.get(System.getProperty("user.home"), ".latte", "log");

    public Resource getLog(String logPath) {
        return new FileSystemResource(LOG_ROOT_DIR.resolve(logPath));
    }

    public String save(String consoleLog) {
        prepareLogDirectory();

        Path logPath;

        while (true) {
            String logFileName = "latte-" + UUID.randomUUID() + ".log";
            logPath = LOG_ROOT_DIR.resolve(logFileName);
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
        if (LOG_ROOT_DIR.toFile().exists())
            return;

        LOG_ROOT_DIR.toFile().mkdirs();
    }
}
