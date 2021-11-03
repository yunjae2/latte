package com.latte.worker.controller.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@NoArgsConstructor
@Setter
@Getter
public class RunnerRequest {
    @NotBlank
    private String repositoryUrl;
    @NotBlank
    private String branchName;
    @NotNull
    private String username;
    @NotNull
    private String password;
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
