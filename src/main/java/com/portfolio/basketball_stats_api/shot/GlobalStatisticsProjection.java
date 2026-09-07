package com.portfolio.basketball_stats_api.shot;

public interface GlobalStatisticsProjection {

    Long getAttempts();

    Long getMadeShots();

    Long getPointsScored();

    Double getFieldGoalPercentage();
}
