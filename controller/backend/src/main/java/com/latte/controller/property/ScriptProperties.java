package com.latte.controller.property;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import java.nio.file.Path;
import java.nio.file.Paths;

@ConfigurationProperties(prefix = "latte.scripts")
@ConstructorBinding
@Getter
public class ScriptProperties {
    private final Path path;

    public ScriptProperties(String path) {
        this.path = Paths.get(path);
    }
}
