package com.portfolio.basketball_stats_api.player.dto;

import com.portfolio.basketball_stats_api.player.DominantHand;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record CreatePlayerRequest(
        @NotBlank @Schema(example = "Bastian") String name,
        @NotNull @Schema(example = "RIGHT") DominantHand dominantHand,
        @Min(100) @Max(250) @Schema(example = "169", minimum = "100", maximum = "250") Short heightCm
) {
}
