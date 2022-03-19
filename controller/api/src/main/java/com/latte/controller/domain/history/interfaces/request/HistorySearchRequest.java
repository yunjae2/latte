package com.latte.controller.domain.history.interfaces.request;

import com.latte.controller.domain.history.service.SortOrder;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@ToString
public class HistorySearchRequest {
    private final int page;
    private final int size;
    @NotBlank
    private final String orderBy;
    @NotNull
    private final SortOrder order;

    @Builder
    public HistorySearchRequest(int page, int size, String orderBy, SortOrder order) {
        this.page = page;
        this.size = size;
        this.orderBy = orderBy;
        this.order = order;
    }
}
