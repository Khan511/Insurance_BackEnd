package com.example.insurance.infrastructure.web.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUserRequestDto(@NotBlank String firstName, @NotBlank String lastName, @Email String email,
        @Size(min = 4) String password) {
}
