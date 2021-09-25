package com.latte.controller.controller.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
public class HistorySaveRequest {
    private String testName;
    private LocalDateTime startTime;
    private String branchName;
    private String scriptFilePath;
    private String summary;
}
