package com.latte.worker.controller.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.latte.worker.config.WorkerConfig;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class RegisterRequest {
    @Valid
    @NotNull
    @JsonProperty("worker")
    private WorkerConfig workerConfig;
}
