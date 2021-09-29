package com.latte.worker.service;

import com.latte.worker.dto.SourceConfig;
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

    public void fetch(SourceConfig sourceConfig) {
        log.info("Fetching the script repository...");
        FileSystemUtils.deleteRecursively(SOURCE_PATH.toFile());
        try {
            Git.cloneRepository()
                    .setCredentialsProvider(new UsernamePasswordCredentialsProvider(sourceConfig.getToken().getName(), sourceConfig.getToken().getValue()))
                    .setURI(sourceConfig.getRepositoryUrl())
                    .setBranch(sourceConfig.getBranchName())
                    .setDirectory(SOURCE_PATH.toFile())
                    .call();
        } catch (GitAPIException e) {
            log.error("Failed to fetch git repository", e);
        }
    }
}
