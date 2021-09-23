package com.latte.controller.domain;

import lombok.Getter;
import lombok.ToString;

import javax.persistence.Embeddable;

@Getter
@ToString
@Embeddable
public class Latency {
    private Double min;
    private Double max;

    private Double p50;
    private Double p99;
    private Double p99_9;
    private Double p99_99;
}
