package com.latte.worker.controller.request;

import com.latte.worker.type.Token;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class RunnerRequest {
    private String repositoryUrl;
    private Token token;
    private String branchName;
    private String scriptFilePath;
}
