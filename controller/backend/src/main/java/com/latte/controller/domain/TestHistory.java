package com.latte.controller.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
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

    /* TODO: add failed request count */
    private Long requestCount;
    private Long requestedTps;
    private Double actualTps;
    private Double duration;  // in ms

    /* TODO: add iteration count */
    /* TODO: add vuser count (min, max) */

    @Embedded
    private Latency latency;

    @Lob
    private String result;
}
