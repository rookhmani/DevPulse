package com.devpulse.api.dto;

import com.devpulse.api.enums.PipelineStatus;

import java.time.Instant;

public record PipelineResponse(
        Long id,
        Long repositoryId,
        String repositoryName,
        String branchName,
        PipelineStatus status,
        Instant triggeredAt,
        Instant completedAt,
        String commitSha,
        String commitMessage,
        Long durationSeconds
) {
}
