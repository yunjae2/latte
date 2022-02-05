package com.latte.controller.property;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import java.nio.file.Path;
import java.nio.file.Paths;

@ConfigurationProperties(prefix = "latte.logs")
@ConstructorBinding
@Getter
public class LogProperties {
    private final Path path;

    public LogProperties(String path) {
        this.path = Paths.get(path);
    }
}
