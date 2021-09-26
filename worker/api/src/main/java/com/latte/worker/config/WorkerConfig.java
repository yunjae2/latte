package com.latte.worker.config;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@JsonRootName("worker")
@JsonSerialize(as = WorkerConfig.class)     // To mitigate infinite recursive due to @RefreshScope's proxy
@ConfigurationProperties(prefix = "worker")
@RefreshScope
@Component
public class WorkerConfig {
    private Controller controller;

    @NoArgsConstructor
    @Getter
    @Setter
    public static class Controller {
        private String url;
    }
}
