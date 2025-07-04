package com.example.insurance.shared.auth;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;
import com.example.insurance.global.config.JwtUtil;
import com.example.insurance.global.config.enums.TokenType;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ApiLogoutHandler implements LogoutHandler {

    private final JwtUtil jwtUtil;
    private final TokenBlackList tokenBlackList;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();

        jwtUtil.getTokenFromRequestCookie(request,
                TokenType.ACCESS.getValue()).ifPresent(tokenBlackList::add);
        jwtUtil.getTokenFromRequestCookie(request,
                TokenType.REFRESH.getValue()).ifPresent(tokenBlackList::add);

        jwtUtil.removeTokenCookies(response);
        logoutHandler.logout(request, response, authentication);
    }

}
