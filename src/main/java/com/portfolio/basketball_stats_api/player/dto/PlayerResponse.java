package com.portfolio.basketball_stats_api.player.dto;

import com.portfolio.basketball_stats_api.player.DominantHand;
import com.portfolio.basketball_stats_api.player.Player;

import java.time.Instant;
import java.util.UUID;

public record PlayerResponse(
        UUID id,
        String name,
        DominantHand dominantHand,
        Short heightCm,
        Instant createdAt
) {
    public static PlayerResponse from(Player player) {
        return new PlayerResponse(
                player.getId(),
                player.getName(),
                player.getDominantHand(),
                player.getHeightCm(),
                player.getCreatedAt());
    }
}
