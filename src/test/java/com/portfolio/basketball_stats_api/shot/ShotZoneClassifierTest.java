package com.portfolio.basketball_stats_api.shot;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ShotZoneClassifierTest {

    private final ShotZoneClassifier classifier = new ShotZoneClassifier();

    @ParameterizedTest(name = "({0}, {1}) -> {2}")
    @CsvSource({
            "0.0,  0.5, RESTRICTED_AREA",
            "1.0,  3.0, PAINT",
            "0.0,  5.0, MID_RANGE_CENTER",
            "-4.0, 3.0, MID_RANGE_LEFT",
            "4.0,  3.0, MID_RANGE_RIGHT",
            "-4.95,4.95, WING_THREE_LEFT",
            "4.95, 4.95, WING_THREE_RIGHT",
            "-7.24,1.94, CORNER_THREE_LEFT",
            "7.24, 1.94, CORNER_THREE_RIGHT",
            "0.0,  7.0, TOP_OF_KEY_THREE",
    })
    void classifiesCoordinatesIntoExpectedZone(BigDecimal x, BigDecimal y, ShotZone expected) {
        assertThat(classifier.classify(x, y)).isEqualTo(expected);
    }

    @Test
    void rejectsNegativeY() {
        assertThatThrownBy(() -> classifier.classify(BigDecimal.ZERO, BigDecimal.valueOf(-1)))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
