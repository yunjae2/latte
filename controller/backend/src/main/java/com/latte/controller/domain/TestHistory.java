package com.latte.controller.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

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

    private Boolean isSuccessful;

    private Long requestCount;
    private Double rps;
    private Long duration;  // in seconds

    @Embedded
    private Latency latency;

    private String result;
}
