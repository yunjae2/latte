package com.latte.worker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class TestConfig {
    private String testName;
    private LocalDateTime startTime;
    private String branchName;
    private String scriptFilePath;
    private String summary;
}
