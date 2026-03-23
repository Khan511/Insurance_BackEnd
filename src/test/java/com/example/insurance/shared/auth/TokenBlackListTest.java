package com.example.insurance.shared.auth;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.insurance.global.config.JwtUtil;

class TokenBlackListTest {

    private JwtUtil jwtUtil;
    private TokenBlackList tokenBlackList;

    @BeforeEach
    void setUp() {
        jwtUtil = mock(JwtUtil.class);
        tokenBlackList = new TokenBlackList(jwtUtil);
    }

    @Test
    void containsReturnsTrueForTokenBeforeItsExpiryTime() {
        when(jwtUtil.extractExpirationFromToken("token-1"))
                .thenReturn(Optional.of(Instant.now().plusSeconds(300)));

        tokenBlackList.add("token-1");

        assertTrue(tokenBlackList.contains("token-1"));
    }

    @Test
    void containsReturnsFalseForExpiredTokenAndEvictsIt() {
        when(jwtUtil.extractExpirationFromToken("expired-token"))
                .thenReturn(Optional.of(Instant.now().minusSeconds(5)));

        tokenBlackList.add("expired-token");

        assertFalse(tokenBlackList.contains("expired-token"));
        assertFalse(tokenBlackList.contains("expired-token"));
    }
}
