package com.devpulse.api.dto;

import com.devpulse.api.enums.PipelineStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record PipelineRequest(
        @NotNull Long repositoryId,
        @NotBlank String branchName,
        @NotNull PipelineStatus status,
        Instant triggeredAt,
        Instant completedAt,
        @NotBlank String commitSha,
        String commitMessage
) {
}
