package com.latte.controller.controller.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RunnerRequest {
    private String testName;
    private String branchName;
    private String scriptFilePath;
    private String duration;  // ex. "10s", "3m"
    private Long rps;
    private Long estimatedLatency;  // ms
    private Long estimatedPeakLatency;    // ms
}
