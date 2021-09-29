package com.latte.controller.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurer;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Slf4j
@Configuration
@EnableWebFlux
public class WebConfig implements WebFluxConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        String hostAddress;
        try {
            hostAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.error("Failed to get host address", e);
            throw new IllegalStateException(e);
        }

        registry.addMapping("/**")
                .allowedOrigins()
                .allowedMethods("*")
                .allowedOriginPatterns("http://localhost*", "http://" + hostAddress + "*");
    }
}
