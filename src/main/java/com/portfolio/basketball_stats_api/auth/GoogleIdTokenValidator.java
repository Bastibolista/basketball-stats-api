package com.portfolio.basketball_stats_api.auth;

import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

/** Accepts only Google-issued ID tokens minted for this client and belonging to an allowlisted, verified email. */
public class GoogleIdTokenValidator implements OAuth2TokenValidator<Jwt> {

    // Google documents both spellings as valid issuers.
    private static final Set<String> ISSUERS = Set.of("https://accounts.google.com", "accounts.google.com");

    private final String clientId;
    private final Set<String> allowedEmails;

    public GoogleIdTokenValidator(String clientId, List<String> allowedEmails) {
        this.clientId = clientId;
        this.allowedEmails = allowedEmails.stream()
                .map(email -> email.trim().toLowerCase(Locale.ROOT))
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public OAuth2TokenValidatorResult validate(Jwt jwt) {
        // Read as String: Jwt#getIssuer() would try to parse "accounts.google.com" as a URL and throw.
        String issuer = jwt.getClaimAsString("iss");
        if (issuer == null || !ISSUERS.contains(issuer)) {
            return failure("Invalid issuer");
        }
        List<String> audience = jwt.getAudience();
        if (audience == null || !audience.contains(clientId)) {
            return failure("Invalid audience");
        }
        String email = jwt.getClaimAsString("email");
        if (email == null || !Boolean.TRUE.equals(jwt.getClaimAsBoolean("email_verified"))) {
            return failure("Email missing or not verified");
        }
        if (!allowedEmails.contains(email.toLowerCase(Locale.ROOT))) {
            return failure("Email not allowed");
        }
        return OAuth2TokenValidatorResult.success();
    }

    private static OAuth2TokenValidatorResult failure(String description) {
        return OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_token", description, null));
    }
}
