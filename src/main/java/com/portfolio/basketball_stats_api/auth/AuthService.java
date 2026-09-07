package com.portfolio.basketball_stats_api.auth;

import com.portfolio.basketball_stats_api.auth.dto.LoginRequest;
import com.portfolio.basketball_stats_api.auth.dto.LoginResponse;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthProperties authProperties;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public AuthService(AuthProperties authProperties, PasswordEncoder passwordEncoder, TokenService tokenService) {
        this.authProperties = authProperties;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    public LoginResponse login(LoginRequest request) {
        boolean usernameMatches = authProperties.username().equals(request.username());
        boolean passwordMatches = usernameMatches
                && passwordEncoder.matches(request.password(), authProperties.passwordHash());

        if (!passwordMatches) {
            throw new BadCredentialsException("Invalid username or password");
        }

        String token = tokenService.issueToken(request.username());
        return LoginResponse.bearer(token, tokenService.getTokenTtlSeconds());
    }
}
