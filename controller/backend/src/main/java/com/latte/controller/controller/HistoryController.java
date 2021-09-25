package com.latte.controller.controller;

import com.latte.controller.controller.request.HistorySaveRequest;
import com.latte.controller.controller.request.HistorySearchRequest;
import com.latte.controller.controller.response.HistorySearchResponse;
import com.latte.controller.service.HistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/history")
public class HistoryController {

    private final HistoryService historyService;

    @GetMapping("/search")
    public Mono<HistorySearchResponse> search(HistorySearchRequest historySearchRequest) {
        /* TODO */
        return null;
    }

    @GetMapping("/all")
    public Mono<HistorySearchResponse> fetchAll() {
        return historyService.getAll()
                .map(HistorySearchResponse::from)
                .doOnSuccess(response -> log.info("response: {}", response));
    }

    /* Called by worker when a test is done */
    @PostMapping
    public Mono<Void> save(@RequestBody HistorySaveRequest historySaveRequest) {
        return historyService.save(historySaveRequest)
                .doOnSuccess(v -> log.info("Saved test history successfully"));
    }
}
