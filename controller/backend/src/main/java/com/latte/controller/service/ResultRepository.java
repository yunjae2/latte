package com.latte.controller.service;

import com.latte.controller.dto.RuntimeStat;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public class ResultRepository {
    private volatile Flux<RuntimeStat> result = Flux.empty();

    public void initialize() {
        this.result = Flux.empty();
    }

    public void update(Flux<RuntimeStat> result) {
        this.result = result;
    }

    public Flux<RuntimeStat> replay() {
        return this.result;
    }
}
