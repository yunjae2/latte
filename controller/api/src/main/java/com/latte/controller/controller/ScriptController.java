package com.latte.controller.controller;

import com.latte.controller.controller.request.ScriptCommitRequest;
import com.latte.controller.controller.response.BranchFindResponse;
import com.latte.controller.controller.response.ScriptFindResponse;
import com.latte.controller.service.ScriptService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

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

    @PostMapping("/commit")
    public Mono<Boolean> commit(@RequestBody @Valid ScriptCommitRequest scriptCommitRequest) {
        return scriptService.commit(scriptCommitRequest.getFileName(), scriptCommitRequest.getContent(), scriptCommitRequest.getMessage());
    }

    @GetMapping("/branch/all")
    public Mono<BranchFindResponse> findBranches() {
        return scriptService.getBranches()
                .map(BranchFindResponse::from);
    }
}
