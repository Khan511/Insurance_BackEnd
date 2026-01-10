package com.example.insurance.shared.kernel.utils;

import java.time.Instant;
import java.util.Map;

import org.springframework.http.HttpStatus;

import jakarta.servlet.http.HttpServletRequest;

public class ResponseBuilder {

    public static ApiResponse buildSuccess(
            HttpServletRequest request,
            Map<String, ?> data,
            String message,
            HttpStatus status) {
        return new ApiResponse(Instant.now(), status.value(), getPath(request), message, null, data);
    }

    public static ApiResponse buildError(HttpServletRequest request,
            String error,
            String message,
            HttpStatus status) {

        return new ApiResponse(Instant.now(), status.value(), getPath(request), message, error, null);

    }

    private static String getPath(HttpServletRequest request) {
        return request.getRequestURI();
    }
}
