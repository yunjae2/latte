package com.latte.controller.controller.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class HistoryDeleteResponse {
    private Boolean success;

    public static HistoryDeleteResponse from(Boolean success) {
        return new HistoryDeleteResponse(success);
    }
}
