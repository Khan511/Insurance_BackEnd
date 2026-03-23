package com.example.insurance.shared.auth;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import com.example.insurance.global.config.JwtUtil;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TokenBlackList {

    // A thread-safe map to store blacklisted tokens (token string -> true)
    // Using ConcurrentHashMap ensures that multiple threads can safely add/check
    // tokens simultaneously.
    // It's thread-safe, meaning multiple requests (threads) can access it without
    // causing data corruption.
    // It's faster than Collections.synchronizedMap() in concurrent environments.
    // Old code:
    // private final ConcurrentHashMap<String, Boolean> blacklistedTokens = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Instant> blacklistedTokens = new ConcurrentHashMap<>();

    private static final Duration FALLBACK_BLACKLIST_TTL = Duration.ofHours(1);

    private final JwtUtil jwtUtil;

    // Adds a token to the blacklist
    public void add(String token) {
        // Old code:
        // blacklistedTokens.put(token, true);
        pruneExpiredTokens();

        Instant expiresAt = jwtUtil.extractExpirationFromToken(token)
                .orElse(Instant.now().plus(FALLBACK_BLACKLIST_TTL));

        blacklistedTokens.put(token, expiresAt);
    }

    // Checks whether a token is in the blacklist
    public boolean contains(String accessToken) {
        // Old code:
        // return blacklistedTokens.containsKey(accessToken);
        pruneExpiredTokens();

        Instant expiresAt = blacklistedTokens.get(accessToken);
        if (expiresAt == null) {
            return false;
        }

        if (!expiresAt.isAfter(Instant.now())) {
            blacklistedTokens.remove(accessToken);
            return false;
        }

        return true;
    }

    private void pruneExpiredTokens() {
        Instant now = Instant.now();
        blacklistedTokens.entrySet().removeIf(entry -> !entry.getValue().isAfter(now));
    }
}
