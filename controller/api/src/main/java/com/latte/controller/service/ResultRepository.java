package com.latte.controller.service;

import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import javax.annotation.PostConstruct;

@Repository
public class ResultRepository {
    private static final Flux<String> INITIAL_RESULT = Flux.just("No previous result exists");

    private volatile Flux<String> result;

    @PostConstruct
    public void initialize() {
        this.result = INITIAL_RESULT;
    }

    public void update(Flux<String> result) {
        this.result = result;
    }

    public Flux<String> replay() {
        return this.result;
    }
}
