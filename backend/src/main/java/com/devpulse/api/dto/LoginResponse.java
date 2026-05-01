package com.devpulse.api.dto;

import com.devpulse.api.enums.UserRole;

public record LoginResponse(String token, String username, String email, UserRole role) {
}
