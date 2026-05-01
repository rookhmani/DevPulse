package com.devpulse.api.dto;

import com.devpulse.api.enums.PipelineStatus;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record PipelineStatusRequest(@NotNull PipelineStatus status, Instant completedAt) {
}
