package com.portfolio.basketball_stats_api.shot;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Classifies a shot's court position into a tactical {@link ShotZone}.
 * Coordinates are in meters with origin at the basket center; y must be >= 0 (toward half court).
 */
@Component
public class ShotZoneClassifier {

    private static final double RESTRICTED_AREA_RADIUS_M = 1.25;
    private static final double THREE_POINT_RADIUS_M = 6.75;
    private static final double PAINT_HALF_WIDTH_M = 2.45;
    private static final double PAINT_LENGTH_M = 4.2;
    private static final double CORNER_ANGLE_DEG = 30.0;
    private static final double WING_ANGLE_DEG = 60.0;

    public ShotZone classify(BigDecimal posX, BigDecimal posY) {
        double x = posX.doubleValue();
        double y = posY.doubleValue();
        if (y < 0) {
            throw new IllegalArgumentException("posY cannot be negative: shots are taken facing the basket");
        }

        double distance = Math.hypot(x, y);
        if (distance <= RESTRICTED_AREA_RADIUS_M) {
            return ShotZone.RESTRICTED_AREA;
        }
        if (Math.abs(x) <= PAINT_HALF_WIDTH_M && y <= PAINT_LENGTH_M) {
            return ShotZone.PAINT;
        }

        double angleDeg = Math.toDegrees(Math.atan2(y, Math.abs(x)));
        boolean isThree = distance >= THREE_POINT_RADIUS_M;
        boolean left = x < 0;

        if (angleDeg >= WING_ANGLE_DEG) {
            return isThree ? ShotZone.TOP_OF_KEY_THREE : ShotZone.MID_RANGE_CENTER;
        }
        if (angleDeg >= CORNER_ANGLE_DEG) {
            if (isThree) {
                return left ? ShotZone.WING_THREE_LEFT : ShotZone.WING_THREE_RIGHT;
            }
            return left ? ShotZone.MID_RANGE_LEFT : ShotZone.MID_RANGE_RIGHT;
        }
        if (isThree) {
            return left ? ShotZone.CORNER_THREE_LEFT : ShotZone.CORNER_THREE_RIGHT;
        }
        return left ? ShotZone.MID_RANGE_LEFT : ShotZone.MID_RANGE_RIGHT;
    }
}
