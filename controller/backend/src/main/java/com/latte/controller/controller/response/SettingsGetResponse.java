package com.latte.controller.controller.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.latte.controller.config.ControllerConfig;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class SettingsGetResponse {
    @JsonProperty("controller")
    private ControllerConfig controllerConfig;

    public static SettingsGetResponse from(ControllerConfig config) {
        return new SettingsGetResponse(config);
    }
}
