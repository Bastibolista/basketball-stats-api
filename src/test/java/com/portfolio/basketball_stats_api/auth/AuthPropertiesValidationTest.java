package com.portfolio.basketball_stats_api.auth;

import jakarta.validation.Validation;
import jakarta.validation.Validator;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AuthPropertiesValidationTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void acceptsStrongConfiguration() {
        assertThat(validator.validate(new AuthProperties("user", "hash", "x".repeat(32)))).isEmpty();
    }

    @Test
    void rejectsBlankValuesAndShortSecret() {
        assertThat(validator.validate(new AuthProperties("", "hash", "x".repeat(32)))).hasSize(1);
        assertThat(validator.validate(new AuthProperties("user", " ", "x".repeat(32)))).hasSize(1);
        assertThat(validator.validate(new AuthProperties("user", "hash", "short"))).hasSize(1);
    }
}
