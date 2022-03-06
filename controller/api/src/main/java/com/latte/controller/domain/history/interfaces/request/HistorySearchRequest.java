package com.latte.controller.domain.history.interfaces.request;

import com.latte.controller.domain.history.service.SortOrder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@ToString
@RequiredArgsConstructor
public class HistorySearchRequest {
    private final int page;
    private final int size;
    @NotBlank
    private final String orderBy;
    @NotNull
    private final SortOrder order;
}
