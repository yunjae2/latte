package com.latte.worker.controller.request;

import com.latte.worker.type.Token;
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
    @NotNull
    private Token token;
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
