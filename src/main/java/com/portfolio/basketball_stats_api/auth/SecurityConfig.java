package com.portfolio.basketball_stats_api.auth;

import com.portfolio.basketball_stats_api.ratelimit.FixedWindowRateLimiter;
import com.portfolio.basketball_stats_api.ratelimit.RateLimitFilter;
import com.portfolio.basketball_stats_api.ratelimit.RateLimitProperties;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;
import org.springframework.security.oauth2.jwt.MappedJwtClaimSetConverter;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.filter.CorsFilter;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.util.Arrays;
import java.util.Map;

/**
 * Shared filter chain. Token validation depends on app.auth.mode: "google" validates Google ID tokens,
 * "local" validates the self-issued dev JWT.
 */
@Configuration
@EnableMethodSecurity
@EnableConfigurationProperties({CorsProperties.class, RateLimitProperties.class})
public class SecurityConfig {

    private static final long RATE_LIMIT_WINDOW_MILLIS = 60_000;

    private final CorsProperties corsProperties;
    private final RateLimitProperties rateLimitProperties;

    public SecurityConfig(CorsProperties corsProperties, RateLimitProperties rateLimitProperties) {
        this.corsProperties = corsProperties;
        this.rateLimitProperties = rateLimitProperties;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtDecoder jwtDecoder,
                                                   JwtAuthenticationConverter jwtAuthenticationConverter) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // stateless bearer-token API, no cookies involved
                .cors(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/login").permitAll()
                    .requestMatchers("/actuator/health", "/actuator/health/**").permitAll()
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt
                        .decoder(jwtDecoder)
                        .jwtAuthenticationConverter(jwtAuthenticationConverter)));

        // Not a @Bean: a Filter bean would also be auto-registered in the servlet container and run twice.
        if (rateLimitProperties.enabled()) {
            http.addFilterAfter(rateLimitFilter(), CorsFilter.class);
        }
        return http.build();
    }

    @Bean
    public org.springframework.web.cors.CorsConfigurationSource corsConfigurationSource() {
        org.springframework.web.cors.CorsConfiguration configuration = new org.springframework.web.cors.CorsConfiguration();
        configuration.setAllowedOrigins(corsProperties.allowedOrigins());
        configuration.setAllowedMethods(java.util.List.of(
                HttpMethod.GET.name(), HttpMethod.POST.name(), HttpMethod.PUT.name(),
                HttpMethod.DELETE.name(), HttpMethod.OPTIONS.name()));
        configuration.setAllowedHeaders(java.util.List.of("Authorization", "Content-Type"));
        configuration.setExposedHeaders(java.util.List.of("Location"));
        configuration.setAllowCredentials(false);

        org.springframework.web.cors.UrlBasedCorsConfigurationSource source =
                new org.springframework.web.cors.UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    private RateLimitFilter rateLimitFilter() {
        Clock clock = Clock.systemUTC();
        return new RateLimitFilter(
                new FixedWindowRateLimiter(clock, rateLimitProperties.loginPerMinute(), RATE_LIMIT_WINDOW_MILLIS),
                new FixedWindowRateLimiter(clock, rateLimitProperties.apiPerMinute(), RATE_LIMIT_WINDOW_MILLIS));
    }

    @Configuration
    @ConditionalOnProperty(name = "app.auth.mode", havingValue = "local")
    @EnableConfigurationProperties(AuthProperties.class)
    static class LocalAuthConfig {

        @Bean
        PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }

        @Bean
        JwtDecoder jwtDecoder(AuthProperties authProperties) {
            SecretKeySpec key = new SecretKeySpec(authProperties.jwtSecret().getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            return NimbusJwtDecoder.withSecretKey(key).build();
        }

        @Bean
        JwtAuthenticationConverter jwtAuthenticationConverter() {
            JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();
            authoritiesConverter.setAuthoritiesClaimName("permissions");
            authoritiesConverter.setAuthorityPrefix(""); // permissions are plain strings, e.g. SHOT_WRITE

            JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
            converter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);
            return converter;
        }
    }

    @Configuration
    @ConditionalOnProperty(name = "app.auth.mode", havingValue = "google")
    @EnableConfigurationProperties(GoogleAuthProperties.class)
    static class GoogleAuthConfig {

        private static final String GOOGLE_JWK_SET_URI = "https://www.googleapis.com/oauth2/v3/certs";

        @Bean
        JwtDecoder jwtDecoder(GoogleAuthProperties properties) {
            NimbusJwtDecoder decoder = NimbusJwtDecoder.withJwkSetUri(GOOGLE_JWK_SET_URI).build();
            // Keep iss as a plain string; the default converter would reject Google's scheme-less variant.
            decoder.setClaimSetConverter(MappedJwtClaimSetConverter.withDefaults(Map.of("iss", Object::toString)));
            decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(
                    new JwtTimestampValidator(),
                    new GoogleIdTokenValidator(properties.clientId(), properties.allowedEmails())));
            return decoder;
        }

        // Access is gated by the email allowlist in the validator, so every allowed user gets every permission.
        @Bean
        JwtAuthenticationConverter jwtAuthenticationConverter() {
            JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
            converter.setJwtGrantedAuthoritiesConverter(jwt -> Arrays.stream(Permission.values())
                    .<GrantedAuthority>map(permission -> new SimpleGrantedAuthority(permission.name()))
                    .toList());
            return converter;
        }
    }
}
