package com.example.insurance.shared.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // Authentication & Authorization
    AUTH_001("INVALID_CREDENTIALS", "Invalid email or password", HttpStatus.UNAUTHORIZED),
    AUTH_002("ACCOUNT_DISABLED", "Your account has been disabled", HttpStatus.UNAUTHORIZED),
    AUTH_003("ACCOUNT_LOCKED", "Account locked due to failed attempts", HttpStatus.UNAUTHORIZED),
    AUTH_004("TOKEN_EXPIRED", "Authentication token expired", HttpStatus.UNAUTHORIZED),
    AUTH_005("INSUFFICIENT_PERMISSIONS", "Insufficient permissions", HttpStatus.FORBIDDEN),

    // Insurance Domain Errors
    INS_001("POLICY_NOT_FOUND", "Insurance policy not found", HttpStatus.NOT_FOUND),
    INS_002("CLAIM_REJECTED", "Claim rejected - review required", HttpStatus.CONFLICT),
    INS_003("PREMIUM_UNPAID", "Premium payment overdue", HttpStatus.PAYMENT_REQUIRED),
    INS_004("COVERAGE_EXPIRED", "Insurance coverage has expired", HttpStatus.CONFLICT),
    INS_005("CLAIM_ALREADY_PROCESSED", "Claim has already been processed", HttpStatus.CONFLICT),
    INS_006("POLICY_EXPIRED", "Policy has expired", HttpStatus.CONFLICT),
    INS_007("INSUFFICIENT_COVERAGE", "Insufficient coverage for this claim", HttpStatus.CONFLICT),

    // Validation Errors
    VAL_001("VALIDATION_FAILED", "Validation failed", HttpStatus.BAD_REQUEST),
    VAL_002("INVALID_EMAIL", "Invalid email format", HttpStatus.BAD_REQUEST),
    VAL_003("WEAK_PASSWORD", "Password doesn't meet security requirements", HttpStatus.BAD_REQUEST),

    // System Errors
    SYS_001("SERVICE_UNAVAILABLE", "Service temporarily unavailable", HttpStatus.SERVICE_UNAVAILABLE),
    SYS_002("TIMEOUT", "Request timeout", HttpStatus.GATEWAY_TIMEOUT),
    SYS_003("DATABASE_ERROR", "Database connection failed", HttpStatus.INTERNAL_SERVER_ERROR),

    RES_001("RESOURCE_NOT_FOUND", "Resource not found", HttpStatus.NOT_FOUND);

    private final String code;
    private final String defaultMessage;
    private final HttpStatus httpStatus;
}