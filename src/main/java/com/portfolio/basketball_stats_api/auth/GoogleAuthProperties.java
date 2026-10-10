package com.portfolio.basketball_stats_api.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
@ConfigurationProperties(prefix = "app.auth.google")
public record GoogleAuthProperties(
        @NotBlank String clientId,
        @NotEmpty List<@NotBlank String> allowedEmails) {
}
