package com.latte.controller.domain.history.infrastructure;

import com.latte.controller.domain.TestHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistoryRepository extends JpaRepository<TestHistory, Long> {
}
