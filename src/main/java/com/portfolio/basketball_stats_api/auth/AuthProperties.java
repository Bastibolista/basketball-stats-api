package com.portfolio.basketball_stats_api.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app.auth")
public record AuthProperties(
        @NotBlank String username,
        @NotBlank String passwordHash,
        // HS256 needs at least 256 bits of key material.
        @NotBlank @Size(min = 32) String jwtSecret) {
}
