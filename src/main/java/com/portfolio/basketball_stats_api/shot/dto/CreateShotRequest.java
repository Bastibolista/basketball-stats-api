package com.portfolio.basketball_stats_api.shot.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateShotRequest(
        @NotNull UUID playerId,
        @NotNull BigDecimal posX,
        @NotNull BigDecimal posY,
        boolean made
) {
}
