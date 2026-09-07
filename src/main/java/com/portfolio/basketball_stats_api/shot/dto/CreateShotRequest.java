package com.portfolio.basketball_stats_api.shot.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateShotRequest(
        @NotNull UUID playerId,
        @NotNull @DecimalMin("-15.0") @DecimalMax("15.0") @Digits(integer = 3, fraction = 2) BigDecimal posX,
        @NotNull @DecimalMin("0.0") @DecimalMax("15.0") @Digits(integer = 3, fraction = 2) BigDecimal posY,
        boolean made
) {
}
