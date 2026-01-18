package com.example.insurance.shared.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Getter
public class BusinessException extends RuntimeException {
    private final String errorCode; // Keep as String for backward compatibility
    private final HttpStatus httpStatus;
    private final Object[] args;
    private Map<String, Object> details;

    // Constructor with ErrorCode enum
    public BusinessException(ErrorCode errorCode, Object... args) {
        super(errorCode.getDefaultMessage());
        this.errorCode = errorCode.getCode();
        this.httpStatus = errorCode.getHttpStatus();
        this.args = args;
    }

    // Constructor with ErrorCode enum and custom message
    public BusinessException(ErrorCode errorCode, String customMessage, Object... args) {
        super(customMessage);
        this.errorCode = errorCode.getCode();
        this.httpStatus = errorCode.getHttpStatus();
        this.args = args;
    }

    // Constructor with ErrorCode enum and details
    public BusinessException(ErrorCode errorCode, Map<String, Object> details, Object... args) {
        super(errorCode.getDefaultMessage());
        this.errorCode = errorCode.getCode();
        this.httpStatus = errorCode.getHttpStatus();
        this.args = args;
        this.details = details;
    }

    // Constructor with ErrorCode enum, custom message, and details
    public BusinessException(ErrorCode errorCode, String customMessage, Map<String, Object> details, Object... args) {
        super(customMessage);
        this.errorCode = errorCode.getCode();
        this.httpStatus = errorCode.getHttpStatus();
        this.args = args;
        this.details = details;
    }

    // Keep existing constructors for backward compatibility
    public BusinessException(String message, String errorCode, HttpStatus httpStatus, Object... args) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.args = args;
    }

    public BusinessException(String message, String errorCode, HttpStatus httpStatus) {
        this(message, errorCode, httpStatus, new Object[] {});
    }

    public BusinessException withDetails(Map<String, Object> details) {
        this.details = details;
        return this;
    }
}