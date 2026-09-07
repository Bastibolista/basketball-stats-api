package com.portfolio.basketball_stats_api.auth;

/** Granted authorities embedded in the issued JWT and checked via {@code @PreAuthorize}. */
public enum Permission {
    PLAYER_READ,
    PLAYER_WRITE,
    SHOT_READ,
    SHOT_WRITE
}
