package com.portfolio.basketball_stats_api.shot.dto;

import com.portfolio.basketball_stats_api.shot.Shot;
import com.portfolio.basketball_stats_api.shot.ShotZone;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ShotResponse(
        UUID id,
        UUID playerId,
        BigDecimal posX,
        BigDecimal posY,
        ShotZone zone,
        boolean made,
        short points,
        Instant takenAt
) {
    public static ShotResponse from(Shot shot) {
        return new ShotResponse(
                shot.getId(),
                shot.getPlayer().getId(),
                shot.getPosX(),
                shot.getPosY(),
                shot.getZone(),
                shot.isMade(),
                shot.getPoints(),
                shot.getTakenAt());
    }
}
