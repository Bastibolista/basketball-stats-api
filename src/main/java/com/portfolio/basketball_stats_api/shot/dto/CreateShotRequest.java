package com.portfolio.basketball_stats_api.shot.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateShotRequest(
        @NotNull @Schema(example = "5f3710e6-2e04-4583-8747-f3b38fb02ab5") UUID playerId,
        @NotNull @DecimalMin("-15.0") @DecimalMax("15.0") @Digits(integer = 3, fraction = 2)
        @Schema(example = "0.0", minimum = "-15.0", maximum = "15.0") BigDecimal posX,
        @NotNull @DecimalMin("0.0") @DecimalMax("15.0") @Digits(integer = 3, fraction = 2)
        @Schema(example = "7.0", minimum = "0.0", maximum = "15.0") BigDecimal posY,
        @Schema(example = "true") boolean made
) {
}
