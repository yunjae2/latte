package com.latte.controller.controller;

import com.latte.controller.controller.request.HistorySearchRequest;
import com.latte.controller.controller.response.HistoryDeleteResponse;
import com.latte.controller.controller.response.HistoryDetailResponse;
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

    @GetMapping("/{id}/detail")
    public Mono<HistoryDetailResponse> getDetail(@PathVariable Long id) {
        return historyService.getDetail(id)
                .map(HistoryDetailResponse::from)
                .doOnSuccess(response -> log.info("response: {}", response))
                .doOnError(error -> log.error("Failed to get test detail of id {}", id, error));
    }

    @GetMapping("/all")
    public Mono<HistorySearchResponse> fetchAll() {
        return historyService.getAll()
                .map(HistorySearchResponse::from)
                .doOnSuccess(response -> log.info("response: {}", response))
                .doOnError(error -> log.error("Failed to fetch all history", error));
    }

    @DeleteMapping
    public Mono<HistoryDeleteResponse> delete(@RequestParam Long id) {
        return historyService.delete(id)
                .map(HistoryDeleteResponse::from)
                .doOnSuccess(response -> log.info("Deleted history with id {}", id))
                .doOnError(error -> log.error("Failed to delete history with id {}", id, error));
    }
}
