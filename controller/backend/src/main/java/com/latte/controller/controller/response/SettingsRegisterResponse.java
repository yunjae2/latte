package com.latte.controller.controller.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class SettingsRegisterResponse {
    private Boolean registered;

    public static SettingsRegisterResponse from(Boolean registered) {
        return new SettingsRegisterResponse(registered);
    }
}
