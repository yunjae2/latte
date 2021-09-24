package com.latte.worker.controller;

import com.latte.worker.controller.request.RunnerRequest;
import com.latte.worker.service.RunnerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/run")
public class RunnerController {
    private final RunnerService runnerService;

    @PostMapping
    public void run(@RequestBody RunnerRequest runnerRequest) {
        runnerService.run(runnerRequest);
    }
}
