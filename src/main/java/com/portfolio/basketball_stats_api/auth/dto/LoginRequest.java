package com.portfolio.basketball_stats_api.auth.dto;

import jakarta.validation.constraints.NotBlank;

import io.swagger.v3.oas.annotations.media.Schema;

public record LoginRequest(
        @NotBlank @Schema(example = "bastian") String username,
        @NotBlank @Schema(example = "changeme123", format = "password") String password
) {
}
