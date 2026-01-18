package com.example.insurance.infrastructure.web.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ResetPasswordRequestDto(

                @NotBlank(message = "Email is required") @Email(message = "Please provied a valid email address") String email) {
}
