package com.devpulse.api.dto;

import com.devpulse.api.enums.PipelineStatus;

import java.time.Instant;

public record RepositoryResponse(
        Long id,
        String name,
        String githubRepoUrl,
        String description,
        Instant createdAt,
        PipelineStatus lastPipelineStatus,
        String lastDeployedVersion
) {
}
