package com.latte.controller.controller.response;

import com.latte.controller.domain.TestHistory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public class HistoryDetailResponse {
    private TestHistory detail;

    public static HistoryDetailResponse from(TestHistory detail) {
        return new HistoryDetailResponse(detail);
    }
}
