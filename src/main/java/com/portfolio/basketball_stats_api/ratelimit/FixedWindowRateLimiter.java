package com.portfolio.basketball_stats_api.ratelimit;

import java.time.Clock;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/** In-memory fixed-window limiter. State is per instance: with several replicas the effective limit is multiplied. */
public class FixedWindowRateLimiter {

    public record Decision(boolean allowed, long retryAfterSeconds) {
    }

    private record Window(long startMillis, int count) {
    }

    private final Clock clock;
    private final int limit;
    private final long windowMillis;
    private final ConcurrentHashMap<String, Window> windows = new ConcurrentHashMap<>();
    private final AtomicLong lastPurgeMillis;

    public FixedWindowRateLimiter(Clock clock, int limit, long windowMillis) {
        this.clock = clock;
        this.limit = limit;
        this.windowMillis = windowMillis;
        this.lastPurgeMillis = new AtomicLong(clock.millis());
    }

    public Decision tryAcquire(String key) {
        long now = clock.millis();
        purgeExpired(now);

        Window window = windows.compute(key, (k, current) ->
                current == null || now - current.startMillis() >= windowMillis
                        ? new Window(now, 1)
                        : new Window(current.startMillis(), current.count() + 1));

        if (window.count() <= limit) {
            return new Decision(true, 0);
        }
        long remainingMillis = windowMillis - (now - window.startMillis());
        return new Decision(false, Math.max(1, (remainingMillis + 999) / 1000));
    }

    // Bounds memory: drops idle keys at most once per window.
    private void purgeExpired(long now) {
        long last = lastPurgeMillis.get();
        if (now - last >= windowMillis && lastPurgeMillis.compareAndSet(last, now)) {
            windows.values().removeIf(w -> now - w.startMillis() >= windowMillis);
        }
    }
}
