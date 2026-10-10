package com.portfolio.basketball_stats_api.ratelimit;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

public class RateLimitFilter extends OncePerRequestFilter {

    private static final String LOGIN_PATH = "/api/auth/login";

    private final FixedWindowRateLimiter loginLimiter;
    private final FixedWindowRateLimiter apiLimiter;

    public RateLimitFilter(FixedWindowRateLimiter loginLimiter, FixedWindowRateLimiter apiLimiter) {
        this.loginLimiter = loginLimiter;
        this.apiLimiter = apiLimiter;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return HttpMethod.OPTIONS.matches(request.getMethod()) || !path.startsWith("/api/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        boolean isLogin = LOGIN_PATH.equals(request.getRequestURI());
        FixedWindowRateLimiter limiter = isLogin ? loginLimiter : apiLimiter;

        // getRemoteAddr only: X-Forwarded-For is client-controlled unless a trusted proxy setup is configured.
        FixedWindowRateLimiter.Decision decision = limiter.tryAcquire(request.getRemoteAddr());
        if (decision.allowed()) {
            chain.doFilter(request, response);
            return;
        }

        response.setStatus(429);
        response.setHeader(HttpHeaders.RETRY_AFTER, String.valueOf(decision.retryAfterSeconds()));
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(String.format(
                "{\"timestamp\":\"%s\",\"status\":429,\"error\":\"Too Many Requests\",\"message\":\"Rate limit exceeded\"}",
                Instant.now()));
    }
}
