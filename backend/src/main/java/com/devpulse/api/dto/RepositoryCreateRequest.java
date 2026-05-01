package com.devpulse.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record RepositoryCreateRequest(
        @NotBlank @Pattern(regexp = "https://github\\.com/[^/]+/[^/]+/?", message = "Must be a GitHub repository URL")
        String githubRepoUrl
) {
}
