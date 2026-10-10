package com.portfolio.basketball_stats_api.auth;

import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class GoogleIdTokenValidatorTest {

    private static final String CLIENT_ID = "123.apps.googleusercontent.com";

    private final GoogleIdTokenValidator validator =
            new GoogleIdTokenValidator(CLIENT_ID, List.of("Me@Example.com"));

    private Jwt token(Map<String, Object> overrides) {
        Map<String, Object> claims = new java.util.HashMap<>(Map.of(
                "iss", "https://accounts.google.com",
                "aud", List.of(CLIENT_ID),
                "sub", "1048",
                "email", "me@example.com",
                "email_verified", true));
        claims.putAll(overrides);
        return new Jwt("t", Instant.now(), Instant.now().plusSeconds(60), Map.of("alg", "RS256"), claims);
    }

    private OAuth2TokenValidatorResult validate(Map<String, Object> overrides) {
        return validator.validate(token(overrides));
    }

    @Test
    void acceptsValidTokenWithCaseInsensitiveEmail() {
        assertThat(validate(Map.of()).hasErrors()).isFalse();
        assertThat(validate(Map.of("iss", "accounts.google.com")).hasErrors()).isFalse();
        assertThat(validate(Map.of("email", "ME@example.com")).hasErrors()).isFalse();
    }

    @Test
    void rejectsWrongIssuer() {
        assertThat(validate(Map.of("iss", "https://evil.example.com")).hasErrors()).isTrue();
    }

    @Test
    void rejectsWrongAudience() {
        assertThat(validate(Map.of("aud", List.of("other-client"))).hasErrors()).isTrue();
    }

    @Test
    void rejectsUnverifiedEmail() {
        assertThat(validate(Map.of("email_verified", false)).hasErrors()).isTrue();
    }

    @Test
    void rejectsEmailOutsideAllowlist() {
        assertThat(validate(Map.of("email", "stranger@example.com")).hasErrors()).isTrue();
    }
}
