package com.latte.controller.service;

import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public class ResultRepository {
    private volatile Flux<String> result = Flux.just("No previous result exists");

    public void update(Flux<String> result) {
        this.result = result;
    }

    public Flux<String> replay() {
        return this.result;
    }
}
