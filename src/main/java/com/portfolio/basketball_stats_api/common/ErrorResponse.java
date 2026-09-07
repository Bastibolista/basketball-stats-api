package com.portfolio.basketball_stats_api.common;

import java.time.Instant;

public record ErrorResponse(Instant timestamp, int status, String error, String message) {
}
