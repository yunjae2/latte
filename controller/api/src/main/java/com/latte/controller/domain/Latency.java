package com.latte.controller.domain;

import lombok.*;

import javax.persistence.Embeddable;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Embeddable
public class Latency {
    private Double min;
    private Double max;
    private Double avg;

    private Double p50;
    private Double p99;
    private Double p99_9;
    private Double p99_99;
}
