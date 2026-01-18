package com.example.insurance.infrastructure.web.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record NewPasswordRequestDto(
                @NotBlank(message = "Token is required") String token,

                @NotBlank(message = "Password is required") @Size(min = 4, message = "Password must be at least 4 characters long") @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$", message = "Password must contain at least one digit, one lowercase, one uppercase letter, one special character and no whitespace") String newPassword,

                @NotBlank(message = "Confirm password is required") String confirmPassword

) {

}
