package com.latte.controller.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;

@Slf4j
@RequiredArgsConstructor
@Builder
@Getter
@Jacksonized
public class RuntimeStat {
    private final Duration time;   // Time spent from the beginning
    private final TestMetrics stat;

    private static final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    public static RuntimeStat from(String raw) {
        try {
            return objectMapper.readValue(raw, RuntimeStat.class);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse runtime test metrics");
            throw new RuntimeException();
        }
    }

    public static boolean hasValidFormat(String test) {
        try {
            objectMapper.readValue(test, RuntimeStat.class);
        } catch (JsonProcessingException e) {
            return false;
        }
        return true;
    }

    public float getSeconds() {
        long seconds = this.time.toSeconds();
        int milliSeconds = this.time.toMillisPart();
        return seconds + (float) milliSeconds / 1000;
    }
}
