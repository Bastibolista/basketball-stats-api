package com.portfolio.basketball_stats_api.shot.dto;

import com.portfolio.basketball_stats_api.shot.GlobalStatisticsProjection;

public record GlobalStatisticsResponse(
        long attempts,
        long madeShots,
        long missedShots,
        double fieldGoalPercentage,
        long pointsScored
) {
    public static GlobalStatisticsResponse from(GlobalStatisticsProjection projection) {
        long attempts = projection.getAttempts();
        long madeShots = projection.getMadeShots();
        return new GlobalStatisticsResponse(
                attempts,
                madeShots,
                attempts - madeShots,
                projection.getFieldGoalPercentage(),
                projection.getPointsScored());
    }
}
