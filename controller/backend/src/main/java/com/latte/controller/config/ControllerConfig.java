package com.latte.controller.config;

import com.latte.controller.type.Token;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@NoArgsConstructor
@Getter
@Setter
@ConfigurationProperties(prefix = "controller")
@RefreshScope
@Component
public class ControllerConfig {
    private Worker worker;
    private Git git;

    @NoArgsConstructor
    @Getter
    @Setter
    public static class Worker {
        private String url;
    }

    @NoArgsConstructor
    @Getter
    @Setter
    public static class Git {
        private String url;
        @NestedConfigurationProperty
        private Token token;
    }
}
