package com.example.insurance.domain.emailService.service;

import org.springframework.scheduling.annotation.Async;

public interface EmailService {
    @Async
    void sendVerificationEmail(String to, String token);

    @Async
    void sendEmail(String to, String subject, String htmlContent);
}