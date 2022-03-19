package com.latte.controller.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
    private Double p9c3;    // p99.9
    private Double p9c4;    // p99.99
}
