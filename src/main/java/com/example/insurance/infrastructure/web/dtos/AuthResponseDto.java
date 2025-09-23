package com.example.insurance.infrastructure.web.dtos;

import java.time.Instant;
import java.util.Set;

import com.example.insurance.shared.kernel.embeddables.PersonName;

import lombok.Builder;

@Builder
public record AuthResponseDto(
                String userId,
                String email, PersonName name, String createdAt, Set<String> roles, Set<String> permissions) {
}
