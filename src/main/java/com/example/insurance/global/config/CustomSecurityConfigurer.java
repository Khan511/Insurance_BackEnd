package com.example.insurance.global.config;

import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import com.example.insurance.domain.user.service.UserService;
import com.example.insurance.shared.auth.TokenBlackList;
import com.example.insurance.shared.constant.Constant;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomSecurityConfigurer extends AbstractHttpConfigurer<CustomSecurityConfigurer, HttpSecurity> {

    private final ObjectMapper objectMapper;
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final TokenBlackList tokenBlackList;
    private final AuthenticationConfiguration authenticationConfiguration;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        // Create Authentication Filter
        JwtAuthenticationFilter authenticationFilter = new JwtAuthenticationFilter(objectMapper, userService, jwtUtil);

        authenticationFilter.setAuthenticationManager(authenticationConfiguration.getAuthenticationManager());
        authenticationFilter.setFilterProcessesUrl(Constant.LOGIN_PATH);

        // Create authorization filter
        JwtAuthorizationFilter authorizationFilter = new JwtAuthorizationFilter(jwtUtil, userDetailsService,
                tokenBlackList);

        // Add filters to security chain
        http.addFilterBefore(authorizationFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(authenticationFilter, authorizationFilter.getClass());
    }

}
