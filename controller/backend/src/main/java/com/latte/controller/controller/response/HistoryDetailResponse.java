package com.latte.controller.controller.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public class HistoryDetailResponse {
    private String detail;

    public static HistoryDetailResponse from(String detail) {
        return new HistoryDetailResponse(detail);
    }
}
