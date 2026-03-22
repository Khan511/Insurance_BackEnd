package com.example.insurance.global.config;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.example.insurance.shared.auth.TokenBlackList;

import jakarta.servlet.FilterChain;

class JwtAuthorizationFilterTest {

    private JwtUtil jwtUtil;
    private TokenBlackList tokenBlackList;
    private JwtAuthorizationFilter filter;

    @BeforeEach
    void setUp() {
        jwtUtil = mock(JwtUtil.class);
        tokenBlackList = mock(TokenBlackList.class);
        filter = new JwtAuthorizationFilter(jwtUtil, mock(CustomUserDetailsService.class), tokenBlackList);
    }

    @Test
    void premiumCalculationRequestBypassesJwtAuthorization() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/premium/calculate");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = mock(FilterChain.class);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtUtil, tokenBlackList);
    }
}
