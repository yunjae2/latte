package com.latte.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class RunConfig {
    private String repositoryUrl;
    private String username;
    private String password;
    private String branchName;
    private String scriptFilePath;
    private String duration;  // ex. "10s", "3m"
    private Long rps;
    private Long estimatedLatency;  // ms
    private Long estimatedPeakLatency;    // ms
}
