package com.example.insurance.global.config.token;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Token {
    private final String access;
    private final String refresh;
}
