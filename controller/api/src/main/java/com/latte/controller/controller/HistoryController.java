package com.latte.controller.controller;

import com.latte.controller.controller.request.HistorySearchRequest;
import com.latte.controller.controller.response.HistoryDeleteResponse;
import com.latte.controller.controller.response.HistoryDetailResponse;
import com.latte.controller.controller.response.HistoryRewriteResponse;
import com.latte.controller.controller.response.HistorySearchResponse;
import com.latte.controller.domain.TestHistory;
import com.latte.controller.service.HistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

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
        return historyService.get(id)
                .map(HistoryDetailResponse::from)
                .doOnSuccess(response -> log.info("Single history fetched; id: {}", id))
                .doOnError(error -> log.error("Failed to get test detail of id {}", id, error));
    }

    @PatchMapping("/{id}/detail")
    public Mono<HistoryDetailResponse> updateName(@PathVariable Long id,
                                                  @RequestBody String name) {
        return historyService.updateName(id, name)
                .map(HistoryDetailResponse::from)
                .doOnSuccess(response -> log.info("The test name of id {} has been updated to {}", id, name))
                .doOnError(error -> log.error("Failed to update the test name of id {} to {}", id, name, error));
    }

    @GetMapping(value = "/{id}/log", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public Mono<? extends Resource> downloadLog(@PathVariable Long id) {
        return historyService.getLog(id)
                .doOnSuccess(response -> log.info("Console log downloaded; id: {}", id))
                .doOnError(error -> log.error("Failed to download the console log of id {}", id, error));
    }

    @GetMapping("/all")
    public Mono<HistorySearchResponse> fetchAll() {
        return historyService.getAll()
                .map(HistorySearchResponse::from)
                .doOnSuccess(response -> log.info("History fetched; ids: {}",
                        response.getRecords().stream()
                                .map(TestHistory::getId)
                                .collect(Collectors.toList()))
                )
                .doOnError(error -> log.error("Failed to fetch all history", error));
    }

    @DeleteMapping
    public Mono<HistoryDeleteResponse> delete(@RequestParam Long id) {
        return historyService.delete(id)
                .map(HistoryDeleteResponse::from)
                .doOnSuccess(response -> log.info("Deleted history with id {}", id))
                .doOnError(error -> log.error("Failed to delete history with id {}", id, error));
    }

    @PostMapping("/rewrite")
    public Mono<HistoryRewriteResponse> rewrite() {
        return historyService.rewriteAll()
                .thenReturn(HistoryRewriteResponse.success())
                .doOnSuccess(response -> log.info("History rewritten successfully"))
                .doOnError(error -> log.error("Failed to rewrite history"));
    }
}
