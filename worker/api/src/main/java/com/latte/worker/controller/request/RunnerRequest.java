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
}
