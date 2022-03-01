package com.latte.controller.repository;

import com.latte.controller.dto.FileInfo;
import com.latte.controller.property.ScriptProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class ScriptRepositoryTest {

    private ScriptRepository scriptRepository;
    Path tempDir;

    @BeforeEach
    void beforeEach(@TempDir Path tempDir) {
        ScriptProperties scriptProperties = new ScriptProperties(tempDir.toAbsolutePath().toString(), tempDir.toAbsolutePath().toString());
        this.scriptRepository = new ScriptRepository(scriptProperties);
        this.tempDir = tempDir;
    }

    @Test
    void tempDirAnnotation() throws IOException {
        // Given
        Path file = Files.createFile(tempDir.resolve("myFile.txt"));
        List<String> expected = List.of("12345", "abc");
        Files.write(file, expected);

        // When
        List<String> actual = Files.readAllLines(file);

        // Then
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void findAll() throws IOException {
        // Given
        Files.createFile(tempDir.resolve("1.txt"));
        Files.createFile(tempDir.resolve("2.txt"));

        Path dir1 = tempDir.resolve("dir1");
        Files.createDirectory(dir1);
        Files.createFile(dir1.resolve("1.txt"));
        Files.createFile(dir1.resolve("2.txt"));

        Path dirA = dir1.resolve("dirA");
        Files.createDirectory(dirA);
        Files.createFile(dirA.resolve("1.txt"));
        Files.createFile(dirA.resolve("2.txt"));

        // When
        List<FileInfo> all = scriptRepository.findAll();
        List<String> actual = all.stream()
                .map(fileInfo -> fileInfo.getName())
                .collect(Collectors.toList());

        // Then
        assertThat(actual).containsExactlyInAnyOrder("1.txt", "2.txt", "dir1/1.txt", "dir1/2.txt", "dir1/dirA/1.txt", "dir1/dirA/2.txt");
    }

    @Test
    void readFile() throws IOException {
        // Given
        Path dir1 = Files.createDirectory(tempDir.resolve("dir1"));
        Path file = Files.createFile(dir1.resolve("myFile.txt"));
        String expected = "12345";
        Files.writeString(file, expected);

        // When
        String actual = scriptRepository.readFile(tempDir.relativize(file).toString());

        // Then
        assertThat(actual).isEqualTo(expected);
    }

}