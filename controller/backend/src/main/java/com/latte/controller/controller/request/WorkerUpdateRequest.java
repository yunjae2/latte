package com.latte.controller.controller.request;

import com.latte.controller.config.ControllerConfig.Worker;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class WorkerUpdateRequest {
    @Valid
    @NotNull
    Worker worker;
}
