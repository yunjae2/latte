package com.latte.worker.dto;

import com.latte.worker.type.Token;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class SourceConfig {
    private String repositoryUrl;
    private String branchName;
    private Token token;
}
