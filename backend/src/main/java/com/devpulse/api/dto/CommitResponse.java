package com.devpulse.api.dto;

import java.time.Instant;

public record CommitResponse(Long id, String sha, String message, String author, Instant timestamp) {
}
