package com.latte.controller.controller.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class RunnerRequest {
    @NotBlank
    private String testName;
    @NotBlank
    private String branchName;
    @NotBlank
    private String scriptFilePath;
    @NotBlank
    private String duration;  // ex. "10s", "3m"
    @NotNull
    private Long rps;
    @NotNull
    private Long estimatedLatency;  // ms
    @NotNull
    private Long estimatedPeakLatency;    // ms
}
