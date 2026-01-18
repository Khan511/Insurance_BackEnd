// File: src/main/java/com/example/insurance/shared/exceptions/GlobalExceptionHandler.java
package com.example.insurance.shared.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final MessageSource messageSource;
    private final ErrorProperties errorProperties;

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponseDto> handleBusinessException(
            BusinessException ex,
            HttpServletRequest request) {

        String traceId = (String) request.getAttribute("traceId");
        if (traceId == null) {
            traceId = generateTraceId();
        }

        // Get localized message
        String localizedMessage = getLocalizedMessage(ex.getErrorCode(), ex.getArgs());

        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .timestamp(LocalDateTime.now())
                .status(ex.getHttpStatus().value())
                .error(ex.getHttpStatus().getReasonPhrase())
                .message(localizedMessage)
                .errorCode(ex.getErrorCode()) // Fixed: Using String from BusinessException
                .path(request.getRequestURI())
                .traceId(traceId)
                .documentationUrl(buildDocumentationUrl(ex.getErrorCode())) // Fixed: Using String
                .metadata(ex.getDetails() != null ? Map.of("details", ex.getDetails()) : null)
                .build();

        logBusinessException(ex, request, traceId);

        return ResponseEntity
                .status(ex.getHttpStatus())
                .header("X-Error-Code", ex.getErrorCode())
                .body(errorResponse);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponseDto> handleAuthenticationException(
            AuthenticationException ex,
            HttpServletRequest request) {

        String traceId = (String) request.getAttribute("traceId");
        String errorCode = resolveAuthenticationErrorCode(ex);

        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .error(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                .message(resolveAuthenticationErrorMessage(ex))
                .errorCode(errorCode)
                .path(request.getRequestURI())
                .traceId(traceId != null ? traceId : generateTraceId())
                .documentationUrl(buildDocumentationUrl(errorCode))
                .metadata(Map.of(
                        "timestamp", LocalDateTime.now().toString(),
                        "clientIp", request.getRemoteAddr(),
                        "userAgent", request.getHeader("User-Agent")))
                .build();

        log.warn("Authentication failed - Code: {}, Path: {}, IP: {}",
                errorCode,
                request.getRequestURI(),
                request.getRemoteAddr());

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .header("X-Error-Code", errorCode)
                .body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        String traceId = (String) request.getAttribute("traceId");

        Map<String, String> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        error -> error.getDefaultMessage() != null
                                ? error.getDefaultMessage()
                                : "Invalid value",
                        (existing, replacement) -> existing));

        List<ErrorResponseDto.SubError> subErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> ErrorResponseDto.SubError.builder()
                        .field(error.getField())
                        .message(error.getDefaultMessage())
                        .rejectedValue(error.getRejectedValue())
                        .build())
                .collect(Collectors.toList());

        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Validation Failed")
                .message("Invalid input data. Please check the errors below.")
                .errorCode("VALIDATION_ERROR")
                .path(request.getRequestURI())
                .traceId(traceId != null ? traceId : generateTraceId())
                .fieldErrors(fieldErrors)
                .subErrors(subErrors)
                .documentationUrl(buildDocumentationUrl("VALIDATION_ERROR"))
                .build();

        log.debug("Validation failed for {}: {}", request.getRequestURI(), fieldErrors);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .header("X-Error-Code", "VALIDATION_ERROR")
                .body(errorResponse);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponseDto> handleAccessDeniedException(
            AccessDeniedException ex,
            HttpServletRequest request) {

        String traceId = (String) request.getAttribute("traceId");

        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.FORBIDDEN.value())
                .error("Access Denied")
                .message("You don't have permission to access this resource")
                .errorCode("ACCESS_DENIED")
                .path(request.getRequestURI())
                .traceId(traceId != null ? traceId : generateTraceId())
                .documentationUrl(buildDocumentationUrl("ACCESS_DENIED"))
                .build();

        log.warn("Access denied: {}", request.getRequestURI());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleResourceNotFoundException(
            ResourceNotFoundException ex,
            HttpServletRequest request) {

        String traceId = (String) request.getAttribute("traceId");

        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .timestamp(LocalDateTime.now())
                .status(ex.getHttpStatus().value())
                .error(ex.getHttpStatus().getReasonPhrase())
                .message(ex.getMessage())
                .errorCode(ex.getErrorCode())
                .path(request.getRequestURI())
                .traceId(traceId != null ? traceId : generateTraceId())
                .documentationUrl(buildDocumentationUrl(ex.getErrorCode()))
                .metadata(Map.of(
                        "resource", ex.getArgs()[0],
                        "identifier", ex.getArgs()[1]))
                .build();

        log.info("Resource not found: {}", ex.getMessage());
        return ResponseEntity.status(ex.getHttpStatus()).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGenericException(
            Exception ex,
            HttpServletRequest request) {

        String traceId = (String) request.getAttribute("traceId");

        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Internal Server Error")
                .message("An unexpected error occurred. Please try again later.")
                .errorCode("INTERNAL_ERROR")
                .path(request.getRequestURI())
                .traceId(traceId != null ? traceId : generateTraceId())
                .documentationUrl(buildDocumentationUrl("INTERNAL_ERROR"))
                .build();

        log.error("Unexpected error occurred at {}: ", request.getRequestURI(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    // Helper methods
    private String getLocalizedMessage(String errorCode, Object[] args) {
        try {
            return messageSource.getMessage(
                    "error." + errorCode.toLowerCase(),
                    args,
                    "An error occurred", // default message
                    LocaleContextHolder.getLocale());
        } catch (Exception e) {
            return "An error occurred"; // fallback message
        }
    }

    private String buildDocumentationUrl(String errorCode) {
        return errorProperties.getDocumentationBaseUrl() +
                "/" + errorCode.toLowerCase().replace("_", "-");
    }

    private String generateTraceId() {
        return java.util.UUID.randomUUID().toString().substring(0, 12);
    }

    private void logBusinessException(BusinessException ex,
            HttpServletRequest request,
            String traceId) {
        if (ex.getHttpStatus().is5xxServerError()) {
            log.error("Business Exception [{}] - {} {} - Trace: {}",
                    ex.getErrorCode(),
                    request.getMethod(),
                    request.getRequestURI(),
                    traceId, ex);
        } else {
            log.warn("Business Exception [{}] - {} {} - Trace: {} - {}",
                    ex.getErrorCode(),
                    request.getMethod(),
                    request.getRequestURI(),
                    traceId,
                    ex.getMessage());
        }
    }

    private String resolveAuthenticationErrorMessage(AuthenticationException exception) {
        return switch (exception) {
            case BadCredentialsException e -> "Invalid email or password";
            case DisabledException e -> "Your account has been disabled. Please contact support.";
            case LockedException e -> "Your account has been locked due to multiple failed login attempts";
            case AccountExpiredException e -> "Your account has expired. Please contact support.";
            case CredentialsExpiredException e -> "Your password has expired. Please reset your password.";
            case InsufficientAuthenticationException e -> "Authentication is required to access this resource";
            default -> "Authentication failed. Please try again.";
        };
    }

    private String resolveAuthenticationErrorCode(AuthenticationException exception) {
        return switch (exception) {
            case BadCredentialsException e -> "INVALID_CREDENTIALS";
            case DisabledException e -> "ACCOUNT_DISABLED";
            case LockedException e -> "ACCOUNT_LOCKED";
            case AccountExpiredException e -> "ACCOUNT_EXPIRED";
            case CredentialsExpiredException e -> "CREDENTIALS_EXPIRED";
            case InsufficientAuthenticationException e -> "INSUFFICIENT_AUTHENTICATION";
            default -> "AUTHENTICATION_FAILED";
        };
    }
}