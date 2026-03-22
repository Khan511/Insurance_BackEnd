package com.example.insurance.domain.user.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.insurance.domain.emailService.service.EmailService;
import com.example.insurance.domain.emailService.userVerificationService.UserVerificationService;
import com.example.insurance.domain.passwordRessetToken.model.PasswordResetTokenEntity;
import com.example.insurance.domain.passwordRessetToken.repository.PasswordResetTokenrepository;
import com.example.insurance.domain.role.model.RoleEntity;
import com.example.insurance.domain.role.repository.RoleRepository;
import com.example.insurance.domain.user.model.User;
import com.example.insurance.domain.user.repository.UserRepository;
import com.example.insurance.global.config.cache.CacheStore;
import com.example.insurance.global.config.enums.LoginType;
import com.example.insurance.shared.enummuration.RoleType;
import com.example.insurance.shared.enummuration.UserStatus;
import com.example.insurance.shared.kernel.embeddables.PersonName;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class userServiceImpl implements UserService {
    private final EmailService emailService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final CacheStore<String, Integer> userCache;
    private final UserVerificationService userVerificationService;
    private final PasswordResetTokenrepository passwordResetTokenrepository;

    @Override
    @Transactional
    public User createUserWithRoles(String firstName, String lastName, String email, String password,
            LocalDate dateOfBirth) {

        // Check if user already exists
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("User with this email already exists");
        }

        User user = new User();
        user.setEmail(email);
        user.setUserId(UUID.randomUUID().toString());
        PersonName name = new PersonName();

        name.setFirstName(firstName);
        name.setLastName(lastName);
        user.setName(name);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setStatus(UserStatus.PENDING_VERIFICATION);
        user.setEnabled(false);
        user.setDateOfBirth(dateOfBirth);
        user.setCreatedAt(Instant.now());

        RoleEntity role = roleRepository.findByName(RoleType.CUSTOMER)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        user.getRoles().add(role);

        User savedUser = userRepository.save(user);

        // Create and send verification email
        try {
            userVerificationService.createVerificationToken(savedUser);

        } catch (Exception e) {

            System.err.println("Failed to send verification email: " + e.getMessage());
        }

        return savedUser;
    }

    @Override
    public User getUserByEmail(String email) {

        return userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public User getUserByUserId(String userId) {
        return userRepository.findUserByUserId(userId).orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public void updateLoginAttempt(String email, LoginType loginType) {

        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        switch (loginType) {
            case LOGIN_FAILURE -> {
                // If the user is not found in the cache (i.e., it's their first attempt),
                // reset their login attempts to 0 and unlock their account.
                if (userCache.get(user.getEmail()) == null) {
                    user.setLoginAttempts(0);
                    user.setAccountNonLocked(true);
                }
                user.setLoginAttempts(user.getLoginAttempts() + 1);
                userCache.put(user.getEmail(), user.getLoginAttempts());
                // If the login attempts exceed 5, lock the user's account.
                if (userCache.get(user.getEmail()) > 5) {
                    user.setAccountNonLocked(false);
                }

            }
            case LOGIN_SUCCESS -> {
                // Handle successful login case
                // If the login is successful, ensure the account is unlocked.
                user.setLoginAttempts(0);
                user.setAccountNonLocked(true);
                user.setLastLogin(LocalDateTime.now());
                // Remove the user from the cache since their login was successful.
                userCache.evict(user.getEmail());
            }
        }
        userRepository.save(user);
    }

    @Override
    @Transactional
    public boolean verifyEmail(String token) {
        try {
            String result = userVerificationService.verifyEmail(token);

            if (result.startsWith("ALREADY_USED:")) {
                // Token was already used
                String email = result.substring("ALREADY_USED:".length());
                log.info("Token already used for email: {}", email);
                return false; // This will trigger failed page
            } else if (result.startsWith("ALREADY_VERIFIED:")) {
                // User already verified
                String email = result.substring("ALREADY_VERIFIED:".length());
                log.info("User already verified: {}", email);
                return true; // This will trigger success page
            } else {
                // Normal successful verification
                log.info("Email verified successfully for: {}", result);
                return true;
            }
        } catch (RuntimeException e) {
            log.error("Email verification failed: {}", e.getMessage());
            return false;
        }
    }

    @Override
    @Transactional
    public void resendVerificationEmail(String email) {
        userVerificationService.resendVerificationEmail(email);
    }

    @Override
    @Transactional
    public void initiatePasswordReset(String email) {
        try {
            log.info("Initiat Method ===========================================");

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

            // Check if user is Vvalid
            if (!user.isEnabled()) {
                throw new RuntimeException("Please verify your email before resetting password");
            }

            // Delete any existing tokens for user
            passwordResetTokenrepository.deleteByUser(user);

            // Create new reset token
            PasswordResetTokenEntity resetTokenEntity = new PasswordResetTokenEntity();
            resetTokenEntity.setToken(UUID.randomUUID().toString());
            resetTokenEntity.setUser(user);
            // 1 hour expiry
            resetTokenEntity.setExpiryDate(Instant.now().plus(1, ChronoUnit.HOURS));
            resetTokenEntity.setUsed(false);

            passwordResetTokenrepository.save(resetTokenEntity);

            // Send password reset email
            emailService.sendPasswordResetEmail(user.getEmail(), resetTokenEntity.getToken());

        } catch (Exception e) {
            log.info("Password reset requested for non-existent email: {}", email);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean validatePasswordResetToken(String token) {
        try {

            PasswordResetTokenEntity resetTokenEntity = passwordResetTokenrepository.findByToken(token)
                    .orElseThrow(() -> new RuntimeException("Invalid reset token"));

            // Check if toekn is valid
            if (resetTokenEntity.getExpiryDate().isBefore(Instant.now())) {
                throw new RuntimeException("Reset token has expired");
            }

            // Check if token has already benn used.
            if (resetTokenEntity.isUsed()) {
                throw new RuntimeException("Reset token has already been used.");
            }

            return true;

        } catch (Exception e) {
            log.error("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    @Override
    @Transactional
    public void resetPassword(String token, String newPassword) {

        PasswordResetTokenEntity resetToken = passwordResetTokenrepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid reset token"));

        // Validate token
        if (resetToken.getExpiryDate().isBefore(Instant.now())) {
            throw new RuntimeException("Reset token has expired");
        }

        if (resetToken.isUsed()) {
            throw new RuntimeException("Reset token has already been used");
        }

        // Update user password
        User user = resetToken.getUser();
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        // Mark token as user
        resetToken.setUsed(true);
        resetToken.setUsedAt(Instant.now());
        passwordResetTokenrepository.save(resetToken);

        // Cleare login attempts cache
        userCache.evict(user.getEmail());

    }

}
