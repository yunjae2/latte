package com.latte.controller.service;

import com.latte.controller.domain.TestHistory;
import com.latte.controller.repository.HistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HistoryService {
    private final HistoryRepository historyRepository;

    public Mono<List<TestHistory>> getAll() {
        return Mono.just(historyRepository.findAll());
    }
}
