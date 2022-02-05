package com.latte.controller.controller.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ScriptCommitRequest {
    @NotBlank
    private String fileName;
    private String content;
    @NotBlank
    private String message;
}
