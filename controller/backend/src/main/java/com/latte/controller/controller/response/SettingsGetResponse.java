package com.latte.controller.controller.response;

import com.latte.controller.config.ControllerConfig.Settings;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class SettingsGetResponse {
    private Settings settings;

    public static SettingsGetResponse from(Settings settings) {
        return new SettingsGetResponse(settings);
    }
}
