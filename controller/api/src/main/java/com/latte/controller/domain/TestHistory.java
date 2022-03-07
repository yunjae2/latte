package com.latte.controller.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Optional;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString
public class TestHistory {
    @GeneratedValue
    @Id
    private Long id;

    private String name;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime date;

    private String branchName;
    private String scriptFilePath;

    /* TODO: replace with exit status of the test */
    private Boolean isSuccessful;

    /* HTTP request stats */
    private Long requestCount;
    private Long successCount;
    private Long failCount;

    private Long requestedTps;
    private Double actualTps;
    private Double duration;  // in ms

    private Long iterationTotal;
    private Long iterationSuccess;
    private Long iterationFail;
    /* TODO: add vuser count (min, max) */

    @Embedded
    private Latency latency;

    @Lob
    private String result;

    private String logPath;

    @Builder(toBuilder = true)
    public TestHistory(Long id,
                       String name,
                       LocalDateTime date,
                       String branchName,
                       String scriptFilePath,
                       Boolean isSuccessful,
                       Long requestCount,
                       Long successCount,
                       Long failCount,
                       Long requestedTps,
                       Double actualTps,
                       Double duration,
                       Long iterationTotal,
                       Long iterationFail,
                       Latency latency,
                       String result,
                       String logPath) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.branchName = branchName;
        this.scriptFilePath = scriptFilePath;
        this.isSuccessful = isSuccessful;
        this.requestCount = requestCount;
        this.successCount = successCount;
        this.failCount = failCount;
        this.requestedTps = requestedTps;
        this.actualTps = actualTps;
        this.duration = duration;
        this.iterationTotal = iterationTotal;
        this.iterationSuccess = Optional.ofNullable(iterationTotal).orElse(0L) - Optional.ofNullable(iterationFail).orElse(0L);
        this.iterationFail = iterationFail;
        this.latency = latency;
        this.result = result;
        this.logPath = logPath;
    }
}
