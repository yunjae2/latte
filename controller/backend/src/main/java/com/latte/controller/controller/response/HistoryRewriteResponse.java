package com.latte.controller.controller.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class HistoryRewriteResponse {
    private Boolean success;

    public static HistoryRewriteResponse success() {
        return new HistoryRewriteResponse(true);
    }
}
