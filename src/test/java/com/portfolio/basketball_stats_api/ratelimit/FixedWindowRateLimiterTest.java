package com.portfolio.basketball_stats_api.ratelimit;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

class FixedWindowRateLimiterTest {

    private static final class MutableClock extends Clock {
        private long millis;

        @Override
        public java.time.ZoneId getZone() {
            return ZoneOffset.UTC;
        }

        @Override
        public Clock withZone(java.time.ZoneId zone) {
            return this;
        }

        @Override
        public Instant instant() {
            return Instant.ofEpochMilli(millis);
        }

        void advance(long ms) {
            millis += ms;
        }
    }

    @Test
    void blocksAfterLimitAndReportsRetryAfter() {
        MutableClock clock = new MutableClock();
        FixedWindowRateLimiter limiter = new FixedWindowRateLimiter(clock, 2, 60_000);

        assertThat(limiter.tryAcquire("a").allowed()).isTrue();
        assertThat(limiter.tryAcquire("a").allowed()).isTrue();

        clock.advance(10_000);
        FixedWindowRateLimiter.Decision blocked = limiter.tryAcquire("a");
        assertThat(blocked.allowed()).isFalse();
        assertThat(blocked.retryAfterSeconds()).isEqualTo(50);
    }

    @Test
    void keysAreIndependent() {
        FixedWindowRateLimiter limiter = new FixedWindowRateLimiter(new MutableClock(), 1, 60_000);

        assertThat(limiter.tryAcquire("a").allowed()).isTrue();
        assertThat(limiter.tryAcquire("b").allowed()).isTrue();
        assertThat(limiter.tryAcquire("a").allowed()).isFalse();
    }

    @Test
    void windowResetsAfterExpiry() {
        MutableClock clock = new MutableClock();
        FixedWindowRateLimiter limiter = new FixedWindowRateLimiter(clock, 1, 60_000);

        limiter.tryAcquire("a");
        assertThat(limiter.tryAcquire("a").allowed()).isFalse();

        clock.advance(60_000);
        assertThat(limiter.tryAcquire("a").allowed()).isTrue();
    }
}
