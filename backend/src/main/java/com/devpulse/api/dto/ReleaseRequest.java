package com.devpulse.api.dto;

import com.devpulse.api.enums.ReleaseEnvironment;
import com.devpulse.api.enums.ReleaseStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record ReleaseRequest(
        @NotNull Long repositoryId,
        @NotBlank String versionTag,
        @NotNull ReleaseEnvironment environment,
        @NotNull ReleaseStatus status,
        Instant deployedAt,
        String notes
) {
}
