package com.example.insurance.infrastructure.web.emailVerificationContorller;

import com.example.insurance.domain.user.service.UserService;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import lombok.RequiredArgsConstructor;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class EmailVerificationController {

    private final UserService userService;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @GetMapping("/verify-email")
    public ResponseEntity<Void> verifyEmail(@RequestParam("token") String token) {
        boolean verified = userService.verifyEmail(token);

        String redirectUrl;

        if (verified) {
            redirectUrl = frontendUrl + "/email-verification/success";
        } else {
            redirectUrl = frontendUrl + "/email-verification/failed";
        }
        return ResponseEntity.status(HttpStatus.FOUND).header("Location", redirectUrl).build();
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<ResendVerificationResponse> resendVerificationEmail(
            @RequestBody ResendVerificationRequest request) {
         userService.resendVerificationEmail(request.getEmail());

        ResendVerificationResponse response = ResendVerificationResponse.builder()
                .message("Verification email sent. Please check your inbox.")
                .email(request.getEmail())
                .timestamp(java.time.Instant.now())
                .build();

        return ResponseEntity.ok(response);
    }

    // Request DTO
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ResendVerificationRequest {
        private String email;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ResendVerificationResponse {
        private String message;
        private String email;
        private Instant timestamp;
    }
}