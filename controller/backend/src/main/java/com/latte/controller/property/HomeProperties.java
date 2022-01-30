package com.latte.controller.property;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import java.nio.file.Path;
import java.nio.file.Paths;

@ConfigurationProperties(prefix = "latte.home")
@ConstructorBinding
@Getter
public class HomeProperties {
    private final Path path;

    public HomeProperties(String path) {
        this.path = Paths.get(path);
    }
}
