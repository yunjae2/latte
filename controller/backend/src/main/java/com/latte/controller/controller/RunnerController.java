package com.latte.controller.controller;

import com.latte.controller.controller.request.RunnerRequest;
import com.latte.controller.service.RunnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@RequestMapping("/run")
public class RunnerController {

    private final RunnerService runnerService;

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> run(@Valid RunnerRequest runnerRequest) {
        return runnerService.run(runnerRequest);
    }

    @GetMapping(path = "/replay", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> replay() {
        return runnerService.replay();
    }

    @PutMapping("/stop")
    public Mono<Boolean> stop() {
        return runnerService.stop();
    }
}
