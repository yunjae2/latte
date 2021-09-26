package com.latte.controller.controller;

import com.latte.controller.controller.request.RunnerRequest;
import com.latte.controller.service.RunnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
@RestController
@RequestMapping("/run")
public class RunnerController {

    private final RunnerService runnerService;

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> run(RunnerRequest runnerRequest) {
        return runnerService.run(runnerRequest);
    }
}
