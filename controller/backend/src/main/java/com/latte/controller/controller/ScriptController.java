package com.latte.controller.controller;

import com.latte.controller.controller.response.ScriptResponse;
import com.latte.controller.service.ScriptService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
@RequestMapping("/script")
public class ScriptController {
    private final ScriptService scriptService;

    @GetMapping("/all")
    public Mono<ScriptResponse> findAll() {
        return scriptService.findAll()
                .map(ScriptResponse::from);
    }
}
