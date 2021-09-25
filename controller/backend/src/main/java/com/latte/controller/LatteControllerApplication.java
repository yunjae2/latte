package com.latte.controller;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties
@SpringBootApplication
public class LatteControllerApplication {

    public static void main(String[] args) {
        SpringApplication.run(LatteControllerApplication.class, args);
    }

}
