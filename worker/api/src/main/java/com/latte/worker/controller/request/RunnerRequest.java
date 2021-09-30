package com.latte.worker.controller.request;

import com.latte.worker.type.Token;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class RunnerRequest {
    private String repositoryUrl;
    private Token token;
    private String branchName;
    private String scriptFilePath;
    private String duration;  // ex. "10s", "3m"
    private Long rps;
    private Long estimatedLatency;  // ms
    private Long estimatedPeakLatency;    // ms
}
