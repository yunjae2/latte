package com.latte.controller.controller.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RunnerRequest {
    private String testName;
    private String branchName;
    private String scriptFilePath;
}
