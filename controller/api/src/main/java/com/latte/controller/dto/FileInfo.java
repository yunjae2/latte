package com.latte.controller.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Slf4j
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class FileInfo {
    private String name;
    private Long size;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastModified;

    public static FileInfo fromPath(Path root, Path path) {
        try {
            return FileInfo.builder()
                    .name(root.relativize(path).toString())
                    .lastModified(LocalDateTime.ofInstant(Files.getLastModifiedTime(path).toInstant(), ZoneId.systemDefault()))
                    .size(Files.size(path))
                    .build();
        } catch (IOException e) {
            log.error("Failed to build FileInfo from {}", path, e);
            throw new IllegalStateException(e);
        }
    }
}
