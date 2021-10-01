package com.latte.worker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class SourceConfig {
    private String repositoryUrl;
    private String branchName;
    private String username;
    private String password;
}
