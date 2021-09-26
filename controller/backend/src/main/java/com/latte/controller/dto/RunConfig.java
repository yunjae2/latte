package com.latte.controller.dto;

import com.latte.controller.type.Token;
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
    private Token token;
    private String branchName;
    private String scriptFilePath;
}
