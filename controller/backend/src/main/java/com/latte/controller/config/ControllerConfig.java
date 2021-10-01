package com.latte.controller.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

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

    @Slf4j
    @Getter
    @Setter
    public static class Git {
        @JsonIgnore
        private final String repositoryUrl;
        @JsonIgnore
        private static final Integer PORT = 8082;
        @JsonIgnore
        private static final String REPOSITORY = "repository";

        private String username;
        private String password;

        public Git() {
            this.repositoryUrl = buildRepositoryUrl();
        }

        public Git(String username, String password) {
            this.repositoryUrl = buildRepositoryUrl();
            this.username = username;
            this.password = password;
        }

        private String buildRepositoryUrl() {
            try {
                return InetAddress.getLocalHost().getHostAddress() + ":" + PORT + "/" + REPOSITORY;
            } catch (UnknownHostException e) {
                log.info("Failed to get host address", e);
                throw new IllegalStateException(e);
            }
        }
    }

    public ControllerConfig toPublicConfig() {
        return ControllerConfig.builder()
                .worker(new Worker(this.getWorker().getUrl()))
                .git(new Git("", ""))
                .build();
    }
}
