package com.latte.controller.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@RequiredArgsConstructor
@Repository
public class ScriptRepository {
    private static final Path SCRIPT_ROOT = Paths.get("/var/www/git/repository");

    public ScriptRepository write(String fileName, String content) {
        try {
            Files.write(SCRIPT_ROOT.resolve(fileName), content.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            log.error("Failed to write content to file {}", fileName);
            throw new IllegalStateException(e);
        }

        return this;
    }

    public ScriptRepository add(String fileName) {
        try {
            Git.open(SCRIPT_ROOT.toFile())
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
            Git.open(SCRIPT_ROOT.toFile())
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
}
