package com.portfolio.basketball_stats_api.shot.dto;

import com.portfolio.basketball_stats_api.shot.ShotZone;
import com.portfolio.basketball_stats_api.shot.ZoneStatisticsProjection;

public record ZoneStatisticsResponse(
        ShotZone zone,
        long attempts,
        long madeShots,
        long missedShots,
        double fieldGoalPercentage,
        long pointsScored
) {
    public static ZoneStatisticsResponse from(ZoneStatisticsProjection projection) {
        long attempts = projection.getAttempts();
        long madeShots = projection.getMadeShots();
        return new ZoneStatisticsResponse(
                projection.getZone(),
                attempts,
                madeShots,
                attempts - madeShots,
                projection.getFieldGoalPercentage(),
                projection.getPointsScored());
    }
}
