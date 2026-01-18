package com.example.insurance.shared.exceptions;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends BusinessException {
    public ResourceNotFoundException(String resource, String identifier) {
        super(
                String.format("%s not found with identifier: %s", resource, identifier),
                "RESOURCE_NOT_FOUND",
                HttpStatus.NOT_FOUND,
                resource,
                identifier);
    }
}
