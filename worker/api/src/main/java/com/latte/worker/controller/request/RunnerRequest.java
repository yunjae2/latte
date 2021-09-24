package com.latte.worker.controller.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class RunnerRequest {
    private String repositoryUrl;
    private Token token;
    private String branchName;
    private String scriptFilePath;

    @NoArgsConstructor
    @Getter
    public static class Token {
        private String name;
        private String value;
    }
}
