package com.portfolio.basketball_stats_api.auth;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.auth")
public record AuthProperties(String username, String passwordHash, String jwtSecret) {
}
