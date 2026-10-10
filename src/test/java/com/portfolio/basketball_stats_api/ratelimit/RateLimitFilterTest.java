package com.portfolio.basketball_stats_api.ratelimit;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.time.Clock;

import static org.assertj.core.api.Assertions.assertThat;

class RateLimitFilterTest {

    private final RateLimitFilter filter = new RateLimitFilter(
            new FixedWindowRateLimiter(Clock.systemUTC(), 2, 60_000),
            new FixedWindowRateLimiter(Clock.systemUTC(), 3, 60_000));

    private MockHttpServletResponse call(String method, String uri, String ip) throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest(method, uri);
        request.setRemoteAddr(ip);
        MockHttpServletResponse response = new MockHttpServletResponse();
        filter.doFilter(request, response, new MockFilterChain());
        return response;
    }

    @Test
    void loginUsesStricterLimitAndReturns429WithRetryAfter() throws Exception {
        call("POST", "/api/auth/login", "1.1.1.1");
        call("POST", "/api/auth/login", "1.1.1.1");

        MockHttpServletResponse blocked = call("POST", "/api/auth/login", "1.1.1.1");

        assertThat(blocked.getStatus()).isEqualTo(429);
        assertThat(blocked.getHeader("Retry-After")).isNotNull();
        assertThat(blocked.getContentAsString()).contains("\"status\":429");
    }

    @Test
    void apiLimitIsSeparateFromLoginAndPerIp() throws Exception {
        for (int i = 0; i < 3; i++) {
            assertThat(call("GET", "/api/players", "1.1.1.1").getStatus()).isEqualTo(200);
        }
        assertThat(call("GET", "/api/players", "1.1.1.1").getStatus()).isEqualTo(429);
        assertThat(call("GET", "/api/players", "2.2.2.2").getStatus()).isEqualTo(200);
    }

    @Test
    void ignoresNonApiPathsAndPreflight() throws Exception {
        for (int i = 0; i < 10; i++) {
            assertThat(call("GET", "/actuator/health", "1.1.1.1").getStatus()).isEqualTo(200);
            assertThat(call("OPTIONS", "/api/players", "1.1.1.1").getStatus()).isEqualTo(200);
        }
    }
}
