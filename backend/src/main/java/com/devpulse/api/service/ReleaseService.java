package com.devpulse.api.service;

import com.devpulse.api.dto.ReleaseRequest;
import com.devpulse.api.dto.ReleaseResponse;
import com.devpulse.api.entity.Release;
import com.devpulse.api.entity.RepositoryEntity;
import com.devpulse.api.repository.ReleaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReleaseService {
    private final ReleaseRepository releaseRepository;
    private final RepositoryService repositoryService;

    public List<ReleaseResponse> findByRepository(Long repositoryId) {
        return releaseRepository.findByRepositoryIdOrderByDeployedAtDesc(repositoryId).stream().map(this::toResponse).toList();
    }

    public List<ReleaseResponse> findAll() {
        return releaseRepository.findAll().stream().map(this::toResponse).toList();
    }

    public ReleaseResponse create(ReleaseRequest request) {
        RepositoryEntity repository = repositoryService.getEntity(request.repositoryId());
        Release release = Release.builder()
                .repository(repository)
                .versionTag(request.versionTag())
                .environment(request.environment())
                .status(request.status())
                .deployedAt(request.deployedAt() == null ? Instant.now() : request.deployedAt())
                .notes(request.notes())
                .build();
        return toResponse(releaseRepository.save(release));
    }

    private ReleaseResponse toResponse(Release release) {
        return new ReleaseResponse(
                release.getId(),
                release.getRepository().getId(),
                release.getRepository().getName(),
                release.getVersionTag(),
                release.getEnvironment(),
                release.getStatus(),
                release.getDeployedAt(),
                release.getNotes()
        );
    }
}
