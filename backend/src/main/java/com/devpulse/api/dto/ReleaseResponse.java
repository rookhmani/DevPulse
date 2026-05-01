package com.devpulse.api.dto;

import com.devpulse.api.enums.ReleaseEnvironment;
import com.devpulse.api.enums.ReleaseStatus;

import java.time.Instant;

public record ReleaseResponse(
        Long id,
        Long repositoryId,
        String repositoryName,
        String versionTag,
        ReleaseEnvironment environment,
        ReleaseStatus status,
        Instant deployedAt,
        String notes
) {
}
