package com.latte.controller.repository;

import com.latte.controller.dto.FileInfo;
import com.latte.controller.property.ScriptProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Repository
public class ScriptRepository {
    private final ScriptProperties scriptProperties;

    public ScriptRepository write(String fileName, String content) {
        try {
            Files.write(scriptProperties.getPath().resolve(fileName), content.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            log.error("Failed to write content to file {}", fileName);
            throw new IllegalStateException(e);
        }

        return this;
    }

    public ScriptRepository add(String fileName) {
        try {
            Git.open(scriptProperties.getPath().toFile())
                    .add()
                    .addFilepattern(fileName)
                    .call();
        } catch (GitAPIException e) {
            log.error("Failed to add file {}", fileName, e);
            throw new IllegalStateException(e);
        } catch (IOException e) {
            log.error("Failed to open the script repository", e);
            throw new IllegalStateException(e);
        }

        return this;
    }

    public ScriptRepository commit(String message) {
        try {
            Git.open(scriptProperties.getPath().toFile())
                    .commit()
                    .setMessage(message)
                    .call();
        } catch (GitAPIException e) {
            log.error("Failed to commit with message {}", message, e);
            throw new IllegalStateException(e);
        } catch (IOException e) {
            log.error("Failed to open the script repository", e);
            throw new IllegalStateException(e);
        }

        return this;
    }

    public List<String> getBranches() {
        List<Ref> refs;
        try {
            refs = Git.open(scriptProperties.getPath().toFile())
                    .branchList()
                    .call();
        } catch (GitAPIException e) {
            log.error("Failed to list branches", e);
            throw new IllegalStateException(e);
        } catch (IOException e) {
            log.error("Failed to open the script repository", e);
            throw new IllegalStateException(e);
        }

        return refs.stream()
                .map(Ref::getName)
                .map(fullName -> fullName.split("/"))
                .map(fields -> fields[fields.length - 1])
                .collect(Collectors.toList());
    }

    public List<FileInfo> findAll() {
        try {
            return Files.walk(scriptProperties.getPath())
                    .filter(Files::isRegularFile)
                    .filter(path -> !scriptProperties.getPath().relativize(path).startsWith(".git"))
                    .map(path -> FileInfo.fromPath(scriptProperties.getPath(), path))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.error("Failed during building file tree", e);
            throw new IllegalStateException(e);
        }
    }

    public String readFile(String fileName) {
        try {
            return Files.readString(scriptProperties.getPath().resolve(fileName));
        } catch (IOException e) {
            log.error("Failed to read file {}", fileName, e);
            throw new IllegalStateException(e);
        }
    }
}
