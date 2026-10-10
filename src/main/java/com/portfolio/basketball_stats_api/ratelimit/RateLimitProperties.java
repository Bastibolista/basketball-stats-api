package com.portfolio.basketball_stats_api.ratelimit;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.rate-limit")
public record RateLimitProperties(boolean enabled, int loginPerMinute, int apiPerMinute) {
}
