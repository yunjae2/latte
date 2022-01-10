package com.latte.worker.repository;

import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class TestProcessRepository {
    private Process process;

    public void update(Process process) {
        this.process = process;
    }

    public void delete() {
        Optional.ofNullable(this.process)
                .ifPresent(Process::destroy);
    }
}
