package com.portfolio.basketball_stats_api.player.dto;

import com.portfolio.basketball_stats_api.player.DominantHand;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record CreatePlayerRequest(
        @NotBlank String name,
        @NotNull DominantHand dominantHand,
        @Min(100) @Max(250) Short heightCm
) {
}
