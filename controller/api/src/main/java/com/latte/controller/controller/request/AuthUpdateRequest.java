package com.latte.controller.controller.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AuthUpdateRequest {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
}
