package com.example.insurance.infrastructure.web.user;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.insurance.domain.user.service.UserService;
import com.example.insurance.shared.kernel.utils.ResponseBuilder;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @PostMapping("/create-user")
    public ResponseEntity<?> createUser(@Valid @RequestBody CreateUserRequestDto userDto, HttpServletRequest request) {
        userService.createUserWithRoles(userDto.firstName(), userDto.lastName(), userDto.email(),
                userDto.password(), userDto.dateOfBirth());
        return ResponseEntity.ok().body(ResponseBuilder.buildSuccess(request, Map.of(),
                "User Created, Please check email for account verification.", HttpStatus.CREATED));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ResetPasswordRequestDto requestDto,
            HttpServletRequest request) {

        log.info("Initiating pawword reset ===========================================");
        userService.initiatePasswordReset(requestDto.email());
        return ResponseEntity.ok().body(ResponseBuilder.buildSuccess(request, Map.of(),
                "If an account exists with this email, you will receive a password reset link.", HttpStatus.OK));
    }

    @PostMapping("/validate-reset-token")
    public ResponseEntity<?> validateResetToken(@Valid @RequestBody PasswordResetTokenRequestDto requestDto,
            HttpServletRequest request) {
        boolean isValid = userService.validatePasswordResetToken(requestDto.token());
        if (!isValid) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseBuilder.buildError(request, "Invalid or expired reset token.", null,
                            HttpStatus.BAD_REQUEST));
        }
        return ResponseEntity.ok().body(
                ResponseBuilder.buildSuccess(request, Map.of("valid", true), "Reset token is valid", HttpStatus.OK));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody NewPasswordRequestDto requestDto,
            HttpServletRequest request) {

        if (!requestDto.newPassword().equals(requestDto.confirmPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseBuilder.buildError(request, "Passwords do not match.", null, HttpStatus.BAD_REQUEST));
        }

        // Fix: Actually call the service method
        userService.resetPassword(requestDto.token(), requestDto.newPassword());

        return ResponseEntity.ok().body(ResponseBuilder.buildSuccess(request, Map.of(),
                "Password has been reset successfully. You can now login with your new password.", HttpStatus.OK));
    }
}