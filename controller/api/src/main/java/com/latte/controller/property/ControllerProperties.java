package com.latte.controller.property;

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
@JsonRootName("controller")
@JsonSerialize(as = ControllerProperties.class)     // To mitigate infinite recursive due to @RefreshScope's proxy
@ConfigurationProperties(prefix = "controller")
@RefreshScope
@Component
public class ControllerProperties {
    private Boolean registered;
    private Settings settings;

    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Getter
    @Setter
    public static class Settings {
        private String workerUrl;
        private String username;
        /* TODO: Use hashed value */
        private String password;

        public Settings toPublicSettings() {
            return new Settings(workerUrl, "", "");
        }
    }
}
