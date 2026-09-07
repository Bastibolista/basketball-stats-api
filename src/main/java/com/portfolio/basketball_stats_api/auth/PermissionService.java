package com.portfolio.basketball_stats_api.auth;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * TEMPORARY: this is a single-user app, so every authenticated user gets every permission.
 * Mirrors the shape of a real permission service (e.g. a DB-backed lookup or an external
 * policy service) so it can be swapped in later without touching callers.
 */
@Service
public class PermissionService {

    public Set<String> getPermissionsFor(String username) {
        return Arrays.stream(Permission.values())
                .map(Enum::name)
                .collect(Collectors.toUnmodifiableSet());
    }
}
