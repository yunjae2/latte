package com.latte.controller.controller.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.latte.controller.config.ControllerConfig;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class SettingsUpdateRequest {
    @Valid
    @NotNull
    @JsonProperty("controller")
    private ControllerConfig controllerConfig;
}
