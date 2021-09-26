package com.latte.worker.controller;

import com.latte.worker.controller.request.RunnerRequest;
import com.latte.worker.service.RunnerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/run")
public class RunnerController {
    private final RunnerService runnerService;

    /* TODO: allow only controller requests; use properties? */
    @PostMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> run(@RequestBody RunnerRequest runnerRequest) {
        return runnerService.run(runnerRequest);
    }
}
