package com.latte.controller;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties
@SpringBootApplication
public class LatteControllerApplication {

    /* TODO: prohibit API accesses when not registered */
    public static void main(String[] args) {
        SpringApplication.run(LatteControllerApplication.class, args);
    }

}
