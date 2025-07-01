package com.example.insurance.global.config;

import static com.example.insurance.shared.constant.Constant.LOGIN_EMAIL;
import java.io.IOException;
import java.time.Instant;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;
import com.example.insurance.domain.user.model.User;
import com.example.insurance.domain.user.service.UserService;
import com.example.insurance.global.config.dto.LoginRequestDto;
import com.example.insurance.global.config.enums.TokenType;
import com.example.insurance.infrastructure.web.dtos.AuthResponseDto;
import com.example.insurance.infrastructure.web.user.UserMapper;
import static com.example.insurance.global.config.enums.LoginType.LOGIN_ATTEMPT;
import static com.example.insurance.global.config.enums.LoginType.LOGIN_FAILURE;
import static com.example.insurance.global.config.enums.LoginType.LOGIN_SUCCESS;
import com.example.insurance.shared.constant.Constant;
import com.example.insurance.shared.kernel.utils.ResponseBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
// @Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final ObjectMapper objectMapper;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    @Override
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        super.setAuthenticationManager(authenticationManager);
        setFilterProcessesUrl(Constant.LOGIN_PATH);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {

        try {
            LoginRequestDto credentials = objectMapper.readValue(request.getInputStream(), LoginRequestDto.class);
            request.setAttribute(LOGIN_EMAIL, credentials.email());

            try {
                userService.updateLoginAttempt(credentials.email(), LOGIN_ATTEMPT);
            } catch (Exception e) {
                log.error("Login attempt update failed for {}", credentials.email());
            }

            return getAuthenticationManager()
                    .authenticate(new UsernamePasswordAuthenticationToken(
                            credentials.email(),
                            credentials.password(),
                            // At this stage (login attempt), we don't have user roles yet,
                            // so we pass an empty list of authorities.
                            // Spring Security will load actual authorities after authentication.
                            Collections.emptyList()));

        } catch (IOException e) {
            throw new AuthenticationServiceException("Authentication request parsing failed", e);
        }
    }

    @Override
    protected void successfulAuthentication(
            HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication)
            throws IOException {

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        User userEntity = customUserDetails.getUserEntity();
        AuthResponseDto user = UserMapper.toAuthResponseDto(userEntity);

        userService.updateLoginAttempt(userEntity.getEmail(), LOGIN_SUCCESS);
        setTokenCookies(response, user);
        writeSuccessResponse(request, response, user);

    }

    @Override
    protected void unsuccessfulAuthentication(
            HttpServletRequest request, HttpServletResponse response, AuthenticationException failed)
            throws IOException {
        String email = (String) request.getAttribute(LOGIN_EMAIL);

        if (email != null) {
            userService.updateLoginAttempt(email, LOGIN_FAILURE);
        }
        writeErrorResponse(request, response, failed);
    }

    private void setTokenCookies(HttpServletResponse response, AuthResponseDto user) {
        jwtUtil.setTokenCookieInResponse(response, user, TokenType.ACCESS);
        jwtUtil.setTokenCookieInResponse(response, user, TokenType.REFRESH);
    }

    private void writeSuccessResponse(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthResponseDto user) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.OK.value());

        objectMapper.writeValue((response.getWriter()),
                ResponseBuilder.buildSuccess(request, Map.of("user", user), "Login successful", HttpStatus.OK));
    }

    private void writeErrorResponse(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());

        Map<String, Object> errorResponse = new LinkedHashMap<>();
        errorResponse.put("timeStamp", Instant.now().toString());
        errorResponse.put("status", HttpStatus.UNAUTHORIZED.value());
        errorResponse.put("error", "Authenticaiton Failed");
        errorResponse.put("message", resolveErrorMessage(exception));
    }

    private String resolveErrorMessage(AuthenticationException exception) {
        return switch (exception) {
            case BadCredentialsException e -> "Invalid credentials";
            case DisabledException e -> "Account disabled";
            case LockedException e -> "Account locked";
            case AccountExpiredException e -> "Account expired";
            case CredentialsExpiredException e -> "Credentials expired";
            default -> "Authentication failed";
        };
    }

}
