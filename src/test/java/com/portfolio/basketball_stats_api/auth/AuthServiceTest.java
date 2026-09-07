package com.portfolio.basketball_stats_api.auth;

import com.portfolio.basketball_stats_api.auth.dto.LoginRequest;
import com.portfolio.basketball_stats_api.auth.dto.LoginResponse;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AuthServiceTest {

    private final AuthProperties authProperties = new AuthProperties("bastian", "hashed-password", "test-secret");
    private final PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
    private final TokenService tokenService = mock(TokenService.class);
    private final AuthService authService = new AuthService(authProperties, passwordEncoder, tokenService);

    @Test
    void loginReturnsTokenForValidCredentials() {
        when(passwordEncoder.matches("correct-password", "hashed-password")).thenReturn(true);
        when(tokenService.issueToken("bastian")).thenReturn("signed-jwt");
        when(tokenService.getTokenTtlSeconds()).thenReturn(3600L);

        LoginResponse response = authService.login(new LoginRequest("bastian", "correct-password"));

        assertThat(response.accessToken()).isEqualTo("signed-jwt");
        assertThat(response.tokenType()).isEqualTo("Bearer");
        assertThat(response.expiresInSeconds()).isEqualTo(3600L);
    }

    @Test
    void loginRejectsWrongPassword() {
        when(passwordEncoder.matches("wrong-password", "hashed-password")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(new LoginRequest("bastian", "wrong-password")))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void loginRejectsUnknownUsername() {
        assertThatThrownBy(() -> authService.login(new LoginRequest("someone-else", "any-password")))
                .isInstanceOf(BadCredentialsException.class);
    }
}
