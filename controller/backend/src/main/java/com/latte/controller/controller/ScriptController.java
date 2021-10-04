package com.latte.controller.controller;

import com.latte.controller.controller.response.ScriptFindResponse;
import com.latte.controller.service.ScriptService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
@RequestMapping("/script")
public class ScriptController {
    private final ScriptService scriptService;

    @GetMapping("/all")
    public Mono<ScriptFindResponse> findAll() {
        return scriptService.findAll()
                .map(ScriptFindResponse::from);
    }

    @GetMapping
    public Mono<String> readContent(@RequestParam String fileName) {
        return scriptService.readFile(fileName);
    }
}
