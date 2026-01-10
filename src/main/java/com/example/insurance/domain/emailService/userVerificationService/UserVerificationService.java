package com.example.insurance.domain.emailService.userVerificationService;

import com.example.insurance.common.enummuration.UserStatus;
import com.example.insurance.domain.emailService.emailVerificationToken.VerificationToken;
import com.example.insurance.domain.emailService.service.EmailService;
import com.example.insurance.domain.emailService.verificationTokenRepo.VerificationTokenRepository;
import com.example.insurance.domain.user.model.User;
import com.example.insurance.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserVerificationService {

    private final VerificationTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    // Token validity: 24 hours
    private static final int EXPIRATION_HOURS = 24;

    @Transactional
    public void createVerificationToken(User user) {
        try {

            // Delete existing tokens for this user
            tokenRepository.deleteByUser(user);

            // Create new token
            String token = UUID.randomUUID().toString();
            VerificationToken verificationToken = VerificationToken.builder()
                    .token(token)
                    .user(user)
                    .expiryDate(LocalDateTime.now().plusHours(EXPIRATION_HOURS))
                    .used(false)
                    .build();

            tokenRepository.save(verificationToken);

            // Send verification email
            emailService.sendVerificationEmail(user.getEmail(), token);

            log.info("Verification  =============================================token created for user: {}",
                    user.getEmail());
        } catch (Exception e) {
            log.error("Failed to =========================================== create verification token for user {}: {}",
                    user.getEmail(), e.getMessage());
        }
    }

    @Transactional
    public String verifyEmail(String token) {
        VerificationToken verificationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid verification token"));

        if (verificationToken.isUsed()) {
            return "ALREADY_USED:" + verificationToken.getUser().getEmail();
        }

        if (verificationToken.isExpired()) {
            // throw new RuntimeException("Token has expired. Please request a new
            // verification email.");
            return "TOKKEN EXPIRED:" + verificationToken.getUser().getEmail();
        }

        User user = verificationToken.getUser();

        // Check if user is already verified
        if (user.getStatus() == UserStatus.ACTIVE) {
            verificationToken.setUsed(true);
            verificationToken.setVerifiedAt(LocalDateTime.now());
            tokenRepository.save(verificationToken);
            return "ALREADY_VERIFIED:" + user.getEmail();
        }

        // Update user status
        user.setStatus(UserStatus.ACTIVE);
        user.setEnabled(true);
        user.setEmailVerifiedAt(LocalDateTime.now());
        userRepository.save(user);

        // Update token
        verificationToken.setUsed(true);
        verificationToken.setVerifiedAt(LocalDateTime.now());
        tokenRepository.save(verificationToken);

        log.info("User email verified: {}", user.getEmail());

        return user.getEmail();
    }

    @Transactional
    public void resendVerificationEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getStatus() == UserStatus.ACTIVE) {
            throw new RuntimeException("Email is already verified");
        }

        createVerificationToken(user);
    }

    // Clean up expired tokens daily at 3 AM
    @Scheduled(cron = "0 0 3 * * ?")
    @Transactional
    public void cleanupExpiredTokens() {
        LocalDateTime now = LocalDateTime.now();
        int deleted = tokenRepository.deleteAllExpiredSince(now);
        log.info("Cleaned up {} expired verification tokens", deleted);
    }
}