package com.portfolio.basketball_stats_api.shot;

public interface ZoneStatisticsProjection {

    ShotZone getZone();

    Long getAttempts();

    Long getMadeShots();

    Long getPointsScored();

    Double getFieldGoalPercentage();
}
