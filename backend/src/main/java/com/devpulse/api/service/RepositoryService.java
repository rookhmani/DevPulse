package com.devpulse.api.service;

import com.devpulse.api.dto.CommitResponse;
import com.devpulse.api.dto.RepositoryCreateRequest;
import com.devpulse.api.dto.RepositoryResponse;
import com.devpulse.api.entity.CommitSnapshot;
import com.devpulse.api.entity.Pipeline;
import com.devpulse.api.entity.Release;
import com.devpulse.api.entity.RepositoryEntity;
import com.devpulse.api.exception.BadRequestException;
import com.devpulse.api.exception.ResourceNotFoundException;
import com.devpulse.api.repository.CommitSnapshotRepository;
import com.devpulse.api.repository.PipelineRepository;
import com.devpulse.api.repository.ReleaseRepository;
import com.devpulse.api.repository.RepositoryJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RepositoryService {
    private final RepositoryJpaRepository repositoryRepository;
    private final PipelineRepository pipelineRepository;
    private final ReleaseRepository releaseRepository;
    private final CommitSnapshotRepository commitRepository;
    private final GitHubService gitHubService;

    public List<RepositoryResponse> findAll() {
        return repositoryRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional
    public RepositoryResponse create(RepositoryCreateRequest request) {
        String normalizedUrl = request.githubRepoUrl().replaceAll("/$", "");
        if (repositoryRepository.existsByGithubRepoUrl(normalizedUrl)) {
            throw new BadRequestException("Repository is already tracked");
        }

        GitHubService.GitHubRepositoryData githubData = gitHubService.fetchRepository(normalizedUrl);
        RepositoryEntity entity = RepositoryEntity.builder()
                .name(githubData.name())
                .githubRepoUrl(normalizedUrl)
                .description(githubData.description())
                .createdAt(Instant.now())
                .build();
        RepositoryEntity saved = repositoryRepository.save(entity);

        githubData.commits().forEach(commit -> {
            commit.setRepository(saved);
            commitRepository.save(commit);
        });
        return toResponse(saved);
    }

    public RepositoryEntity getEntity(Long id) {
        return repositoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Repository not found"));
    }

    public List<CommitResponse> getCommits(Long repositoryId) {
        getEntity(repositoryId);
        return commitRepository.findByRepositoryIdOrderByTimestampDesc(repositoryId).stream()
                .map(commit -> new CommitResponse(commit.getId(), commit.getSha(), commit.getMessage(), commit.getAuthor(), commit.getTimestamp()))
                .toList();
    }

    private RepositoryResponse toResponse(RepositoryEntity repository) {
        Pipeline latestPipeline = pipelineRepository.findTopByRepositoryOrderByTriggeredAtDesc(repository).orElse(null);
        Release latestRelease = releaseRepository.findTopByRepositoryOrderByDeployedAtDesc(repository).orElse(null);
        return new RepositoryResponse(
                repository.getId(),
                repository.getName(),
                repository.getGithubRepoUrl(),
                repository.getDescription(),
                repository.getCreatedAt(),
                latestPipeline == null ? null : latestPipeline.getStatus(),
                latestRelease == null ? null : latestRelease.getVersionTag()
        );
    }
}
