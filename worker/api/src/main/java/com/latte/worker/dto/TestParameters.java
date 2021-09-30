package com.latte.worker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class TestParameters {
    private String duration;  // ex. "10s", "3m"
    private Long rps;
    private Long estimatedLatency;  // ms
    private Long estimatedPeakLatency;    // ms
}
