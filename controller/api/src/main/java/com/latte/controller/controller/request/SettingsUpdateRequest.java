package com.latte.controller.controller.request;

import com.latte.controller.property.ControllerProperties.Settings;
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
    private Settings settings;
}
