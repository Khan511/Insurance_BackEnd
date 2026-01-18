package com.example.insurance.domain.emailService.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Async
    public void sendVerificationEmail(String to, String token) {
        String verificationUrl = String.format("%s/api/v1/auth/verify-email?token=%s", baseUrl, token);

        Context context = new Context();
        context.setVariable("verificationUrl", verificationUrl);
        context.setVariable("token", token);

        String htmlContent = templateEngine.process("email/verification", context);
        String subject = "Verify Your Email Address";

        sendEmail(to, subject, htmlContent);
    }

    @Async
    public void sendEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    mimeMessage,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name());

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);
            log.info("Verification email sent to: {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send verification email to: {}", to, e);
            // throw new RuntimeException("Failed to send verification email", e);
        }
    }

    @Async
    @Override
    public void sendPasswordResetEmail(String to, String token) {

        String resetUrl = String.format("%s/reset-password?token=%s", frontendUrl, token);

        LocalDateTime expiryTime = LocalDateTime.now().plusHours(1);
        String formattedExpiry = expiryTime.format(DateTimeFormatter.ofPattern("MMMM dd, yyyy 'at' hh:mm a"));

        Context context = new Context();

        context.setVariable("resetLink", resetUrl);
        context.setVariable("expiryTime", formattedExpiry);
        context.setVariable("expiryHours", 1);

        String htmlContent = templateEngine.process("email/password-reset", context);
        String subject = "Password Reset Request - Secure Insurance Portal";

        sendEmail(to, subject, htmlContent);
        log.info("Password reset email sent to: {}", to);
    }

}