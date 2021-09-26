package com.latte.controller.config;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.latte.controller.type.Token;
import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@JsonRootName("controller")
@JsonSerialize(as = ControllerConfig.class)     // To mitigate infinite recursive due to @RefreshScope's proxy
@ConfigurationProperties(prefix = "controller")
@RefreshScope
@Component
public class ControllerConfig {
    private Worker worker;
    private Git git;

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class Worker {
        private String url;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class Git {
        private String url;
        @NestedConfigurationProperty
        private Token token;
    }

    public ControllerConfig toPublicConfig() {
        return ControllerConfig.builder()
                .worker(new Worker(this.getWorker().getUrl()))
                .git(new Git(this.getGit().getUrl(), new Token("", "")))
                .build();
    }
}
