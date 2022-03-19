package com.latte.controller.domain.history.interfaces.response;

import com.latte.controller.domain.TestHistory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public class HistorySearchResponse {
    private List<TestHistory> records;

    public static HistorySearchResponse from(List<TestHistory> records) {
        return new HistorySearchResponse(records);
    }
}
