package com.example.insurance.infrastructure.web.user;

import jakarta.validation.constraints.NotBlank;

public record PasswordResetTokenRequestDto(

        @NotBlank(message = "Token is required") String token) {

}
