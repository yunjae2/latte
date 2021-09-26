package com.latte.controller.controller;

import com.latte.controller.controller.request.RunnerRequest;
import com.latte.controller.service.RunnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
@RestController
@RequestMapping("/run")
public class RunnerController {

    private final RunnerService runnerService;

    @PostMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> run(@RequestBody RunnerRequest runnerRequest) {
        return runnerService.run(runnerRequest);
    }
}
