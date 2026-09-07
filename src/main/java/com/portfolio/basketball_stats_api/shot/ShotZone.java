package com.portfolio.basketball_stats_api.shot;

/** Tactical court zones used for shot-chart classification; point value drives the entity's points invariant. */
public enum ShotZone {
    RESTRICTED_AREA(2),
    PAINT(2),
    MID_RANGE_LEFT(2),
    MID_RANGE_CENTER(2),
    MID_RANGE_RIGHT(2),
    CORNER_THREE_LEFT(3),
    CORNER_THREE_RIGHT(3),
    WING_THREE_LEFT(3),
    WING_THREE_RIGHT(3),
    TOP_OF_KEY_THREE(3);

    private final int points;

    ShotZone(int points) {
        this.points = points;
    }

    public short pointValue() {
        return (short) points;
    }
}
