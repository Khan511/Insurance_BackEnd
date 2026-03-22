package com.example.insurance.global.config;

import static com.example.insurance.global.config.enums.LoginType.LOGIN_FAILURE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.BadCredentialsException;

import com.example.insurance.domain.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

class JwtAuthenticationFilterTest {

    private UserService userService;
    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        filter = new JwtAuthenticationFilter(objectMapper, userService, mock(JwtUtil.class));
        filter.setAuthenticationManager(mock(AuthenticationManager.class));
    }

    @Test
    void attemptAuthenticationDoesNotIncrementFailedLoginCounterBeforeAuthentication() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("POST");
        request.setRequestURI("/api/user/login");
        request.setContentType("application/json");
        request.setContent("""
                {"email":"user@example.com","password":"wrong-password"}
                """.getBytes());

        AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(mock(Authentication.class));
        filter.setAuthenticationManager(authenticationManager);

        filter.attemptAuthentication(request, new MockHttpServletResponse());

        verify(userService, never()).updateLoginAttempt(eq("user@example.com"), any());
    }

    @Test
    void unsuccessfulAuthenticationIncrementsCounterOnce() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("POST");
        request.setRequestURI("/api/user/login");
        request.setAttribute("loginEmail", "user@example.com");

        filter.unsuccessfulAuthentication(
                request,
                new MockHttpServletResponse(),
                new BadCredentialsException("Invalid email or password"));

        verify(userService).updateLoginAttempt("user@example.com", LOGIN_FAILURE);
    }
}
