package com.portfolio.basketball_stats_api.auth;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Set;

@Service
public class TokenService {

    private static final Duration TOKEN_TTL = Duration.ofHours(12);

    private final AuthProperties authProperties;
    private final PermissionService permissionService;

    public TokenService(AuthProperties authProperties, PermissionService permissionService) {
        this.authProperties = authProperties;
        this.permissionService = permissionService;
    }

    public long getTokenTtlSeconds() {
        return TOKEN_TTL.toSeconds();
    }

    public String issueToken(String username) {
        Set<String> permissions = permissionService.getPermissionsFor(username);
        Instant now = Instant.now();

        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject(username)
                .issueTime(Date.from(now))
                .expirationTime(Date.from(now.plus(TOKEN_TTL)))
                .claim("permissions", permissions)
                .build();

        try {
            JWSSigner signer = new MACSigner(authProperties.jwtSecret().getBytes(StandardCharsets.UTF_8));
            SignedJWT signedJwt = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claims);
            signedJwt.sign(signer);
            return signedJwt.serialize();
        } catch (JOSEException e) {
            throw new IllegalStateException("Failed to sign JWT", e);
        }
    }
}
