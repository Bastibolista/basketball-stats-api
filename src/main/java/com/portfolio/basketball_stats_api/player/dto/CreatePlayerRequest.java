package com.portfolio.basketball_stats_api.player.dto;

import com.portfolio.basketball_stats_api.player.DominantHand;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreatePlayerRequest(
        @NotBlank String name,
        @NotNull DominantHand dominantHand,
        @Positive Short heightCm
) {
}
