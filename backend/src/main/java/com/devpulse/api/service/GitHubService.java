package com.devpulse.api.service;

import com.devpulse.api.entity.CommitSnapshot;
import com.devpulse.api.entity.RepositoryEntity;
import com.devpulse.api.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GitHubService {
    @Value("${github.token:}")
    private String githubToken;

    public GitHubRepositoryData fetchRepository(String githubRepoUrl) {
        try {
            GHRepository ghRepository = connect().getRepository(parseFullName(githubRepoUrl));
            List<CommitSnapshot> commits = new ArrayList<>();
            for (GHCommit commit : ghRepository.listCommits().toList().stream().limit(10).toList()) {
                String author = commit.getCommitShortInfo().getAuthor() == null
                        ? "unknown"
                        : commit.getCommitShortInfo().getAuthor().getName();
                commits.add(CommitSnapshot.builder()
                        .sha(commit.getSHA1())
                        .message(commit.getCommitShortInfo().getMessage())
                        .author(author)
                        .timestamp(commit.getCommitShortInfo().getCommitDate().toInstant())
                        .build());
            }
            return new GitHubRepositoryData(ghRepository.getName(), ghRepository.getDescription(), commits);
        } catch (IOException ex) {
            throw new BadRequestException("Unable to fetch GitHub repository data: " + ex.getMessage());
        }
    }

    private GitHub connect() throws IOException {
        GitHubBuilder builder = new GitHubBuilder();
        if (StringUtils.hasText(githubToken)) {
            builder.withOAuthToken(githubToken);
        }
        return builder.build();
    }

    private String parseFullName(String url) {
        String cleaned = url.replace("https://github.com/", "").replaceAll("/$", "");
        String[] parts = cleaned.split("/");
        if (parts.length != 2) {
            throw new BadRequestException("GitHub URL must look like https://github.com/owner/repo");
        }
        return parts[0] + "/" + parts[1].replace(".git", "");
    }

    public record GitHubRepositoryData(String name, String description, List<CommitSnapshot> commits) {
    }
}
