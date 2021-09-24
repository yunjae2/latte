package com.latte.controller.controller;

import com.latte.controller.controller.request.RunnerRequest;
import com.latte.controller.service.RunnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/run")
public class RunnerController {

    private final RunnerService runnerService;

    /* TODO: return type */
    @PostMapping
    public void run(@RequestBody RunnerRequest runnerRequest) {
        runnerService.run(runnerRequest);
    }
}
