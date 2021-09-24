package com.latte.worker.service;

import com.latte.worker.controller.request.RunnerRequest.Token;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;

import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@RequiredArgsConstructor
@Service
public class TestSourceService {
    private static final Path SOURCE_PATH = Paths.get(System.getProperty("user.home"), ".latte", "workspace");

    public void fetch(String uri, Token token, String branchName) {
        FileSystemUtils.deleteRecursively(SOURCE_PATH.toFile());
        try {
            Git.cloneRepository()
                    .setCredentialsProvider(new UsernamePasswordCredentialsProvider(token.getName(), token.getValue()))
                    .setURI(uri)
                    .setBranch(branchName)
                    .setDirectory(SOURCE_PATH.toFile())
                    .call();
        } catch (GitAPIException e) {
            log.error("Failed to fetch git repository", e);
        }
    }
}
