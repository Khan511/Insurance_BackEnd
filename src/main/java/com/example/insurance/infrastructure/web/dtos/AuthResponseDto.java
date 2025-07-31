package com.example.insurance.infrastructure.web.dtos;

import java.util.Set;
import lombok.Builder;

@Builder
public record AuthResponseDto(
        String userId,
        String email, Set<String> roles, Set<String> permissions) {
}
