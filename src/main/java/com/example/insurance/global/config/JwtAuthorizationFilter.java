package com.example.insurance.global.config;

import static com.example.insurance.shared.constant.Constant.API_CHATBOT;
import static com.example.insurance.shared.constant.Constant.CREATE_USER;
import static com.example.insurance.shared.constant.Constant.FORGOT_PASSWORD;
import static com.example.insurance.shared.constant.Constant.GET_ALL_POLICIES;
import static com.example.insurance.shared.constant.Constant.GET_POLICY_DETAILS;
import static com.example.insurance.shared.constant.Constant.LOGIN_PATH;
import static com.example.insurance.shared.constant.Constant.PREMIUM_CALCULATION_PATH;
import static com.example.insurance.shared.constant.Constant.RESEND_VERIFICATION_PATH;
import static com.example.insurance.shared.constant.Constant.RESET_PASSWORD;
import static com.example.insurance.shared.constant.Constant.TOKEN_INVALIDATED_MSG;
import static com.example.insurance.shared.constant.Constant.VALIDATE_REQUEST_TOKEN;
import static com.example.insurance.shared.constant.Constant.VERIFY_EMAIL_PATH;

import java.io.IOException;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;
import com.example.insurance.global.config.enums.TokenType;
import com.example.insurance.global.config.token.Token;
import com.example.insurance.global.config.token.TokenData;
import com.example.insurance.infrastructure.web.dtos.AuthResponseDto;
import com.example.insurance.shared.auth.TokenBlackList;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final TokenBlackList tokenBlackList;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String uri = request.getRequestURI();

        if (uri.startsWith(LOGIN_PATH) || uri.startsWith(GET_ALL_POLICIES) || uri.startsWith(GET_POLICY_DETAILS)
                || uri.startsWith(CREATE_USER) || uri.startsWith(VERIFY_EMAIL_PATH)
                || uri.startsWith(RESEND_VERIFICATION_PATH) || uri.startsWith(API_CHATBOT)
                || uri.startsWith(FORGOT_PASSWORD) || uri.startsWith(VALIDATE_REQUEST_TOKEN)
                || uri.startsWith(RESET_PASSWORD) || uri.startsWith(PREMIUM_CALCULATION_PATH)

        )

        {

            filterChain.doFilter(request, response);
            return;
        }

        try {
            processAuthorization(request, response, filterChain);
        } catch (Exception e) {
            handleAuthorizationError(response, e);
        }
    }

    private void processAuthorization(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {

        Optional<String> accessToken = jwtUtil.getTokenFromRequestCookie(request, TokenType.ACCESS.getValue());

        if (accessToken.isPresent() && tokenBlackList.contains(accessToken.get())) {
            handleInvalidToken(response);
            return;
        }
        if (accessToken.isPresent() && isTokenValid(accessToken.get())) {
            authenticateFromAccessToken(accessToken.get(), request);
            filterChain.doFilter(request, response);
            return;
        }

        Optional<String> refreshToken = jwtUtil.getTokenFromRequestCookie(request, TokenType.REFRESH.getValue());

        if (refreshToken.isPresent() && tokenBlackList.contains(refreshToken.get())) {
            handleInvalidToken(response);
            return;
        }

        if (refreshToken.isPresent() && isTokenValid(refreshToken.get())) {
            handleRefreshToken(request, response, filterChain, refreshToken.get());
        }
    }

    private void handleRefreshToken(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain,
            String refreshToken) throws IOException, ServletException {
        AuthResponseDto userDto = jwtUtil.extractTokenData(refreshToken, TokenData::getAuthResponseDta);

        if (!hasValidRoles(userDto)) {
            SecurityContextHolder.clearContext();
            filterChain.doFilter(request, response);
            return;
        }

        String newAccessToken = jwtUtil.generateTokenForUser(userDto, Token::getAccess);
        if (newAccessToken != null) {
            authenticateFromAccessToken(newAccessToken, request);
            jwtUtil.setTokenCookieInResponse(response, userDto, TokenType.ACCESS);
        } else {
            SecurityContextHolder.clearContext();
        }
        filterChain.doFilter(request, response);
    }

    private boolean hasValidRoles(AuthResponseDto user) {
        return user != null && user.roles() != null && !user.roles().isEmpty();
    }

    private void authenticateFromAccessToken(String token, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authentication = buildAuthenticationToken(token);
        if (authentication != null) {
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
    }

    private UsernamePasswordAuthenticationToken buildAuthenticationToken(String token) {
        try {
            String email = jwtUtil.extractEmailFromToken(token);
            if (email == null)
                return null;

            CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(email);
            if (jwtUtil.validateToken(token, userDetails)) {
                return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            }
            log.warn("Token validation failed for user: {}", email);
        } catch (Exception e) {
            log.error("User not found for token {}", token, e);
        }

        return null;
    }

    private boolean isTokenValid(String token) {
        return jwtUtil.extractTokenData(token, TokenData::isValid);
    }

    private void handleInvalidToken(HttpServletResponse response) throws IOException {
        SecurityContextHolder.clearContext();
        response.sendError(HttpStatus.UNAUTHORIZED.value(), TOKEN_INVALIDATED_MSG);
    }

    private void handleAuthorizationError(HttpServletResponse response, Exception e) throws IOException {
        log.error("Authorization error {} ", e.getMessage(), e);
        response.sendError(HttpStatus.UNAUTHORIZED.value(), "Authorization failed");
    }

}
