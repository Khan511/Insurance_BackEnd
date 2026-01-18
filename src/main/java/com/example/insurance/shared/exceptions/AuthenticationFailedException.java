package com.example.insurance.shared.exceptions;

import org.springframework.http.HttpStatus;

public class AuthenticationFailedException extends BusinessException {
    public AuthenticationFailedException(String message) {
        super(message, "AUTH_FAILED", HttpStatus.UNAUTHORIZED);
    }

    public AuthenticationFailedException(String message, String errorCode) {
        super(message, errorCode, HttpStatus.UNAUTHORIZED);
    }
}