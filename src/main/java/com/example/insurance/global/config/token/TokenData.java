package com.example.insurance.global.config.token;

import java.util.List;

import org.springframework.security.core.GrantedAuthority;

import com.example.insurance.infrastructure.web.dtos.AuthResponseDto;

import io.jsonwebtoken.Claims;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TokenData {

    private final boolean valid;
    private final List<GrantedAuthority> authorities;
    private final Claims claims;
    private final AuthResponseDto authResponseDta;
}
