package com.example.insurance.shared.kernel.utils;

import java.time.Instant;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_EMPTY)
public record ApiResponse(
        Instant timeStamp,
        int status,
        String path,
        String message,
        String error,
        Map<String, ?> data) {

    public ApiResponse {
        // Compact constructor for validation in record
        if (status < 100 || status > 599) {
            throw new IllegalArgumentException("Invalid HTTP status code");
        }
    }

}
