package com.latte.controller.controller.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RunnerRequest {
    private String testName;
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
