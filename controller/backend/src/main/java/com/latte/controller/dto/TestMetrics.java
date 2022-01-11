package com.latte.controller.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Builder
@Jacksonized
@Getter
public class TestMetrics {
    private final List<Metric> data;

    /* TODO: Throw error when value is not found */
    public long getFinishedIterationCount() {
        return data.stream()
                .filter(metric -> "iterations".equals(metric.getId()))
                .findFirst()
                .map(Metric::getAttributes)
                .map(MetricAttributes::getSample)
                .map(sample -> Long.valueOf(sample.get("count").toString()))
                .orElseGet(() -> {
                    log.error("Iteration count is not present");
                    return 0L;
                });
    }

    /* TODO: Throw error when value is not found */
    public long getCurrentVUCount() {
        return data.stream()
                .filter(metric -> "vus".equals(metric.getId()))
                .findFirst()
                .map(Metric::getAttributes)
                .map(MetricAttributes::getSample)
                .map(sample -> Long.valueOf(sample.get("value").toString()))
                .orElseGet(() -> {
                    log.error("VU value is not present");
                    return 0L;
                });
    }

    /* TODO: Throw error when value is not found */
    public long getCurrentMaxVUCount() {
        return data.stream()
                .filter(metric -> "vus_max".equals(metric.getId()))
                .findFirst()
                .map(Metric::getAttributes)
                .map(MetricAttributes::getSample)
                .map(sample -> Long.valueOf(sample.get("value").toString()))
                .orElseGet(() -> {
                    log.error("Max VU value is not present");
                    return 0L;
                });
    }

    @RequiredArgsConstructor
    @Builder
    @Jacksonized
    @Getter
    public static class Metric {
        private final String type;
        private final String id;
        private final MetricAttributes attributes;
    }

    @RequiredArgsConstructor
    @Builder
    @Jacksonized
    @Getter
    public static class MetricAttributes {
        private final String type;
        private final String contains;
        private final String tainted;
        private final Map<String, Object> sample;
    }
}
