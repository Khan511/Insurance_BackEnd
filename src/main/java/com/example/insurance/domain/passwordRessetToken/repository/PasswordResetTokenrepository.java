package com.example.insurance.domain.passwordRessetToken.repository;

import java.time.Instant;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.insurance.domain.passwordRessetToken.model.PasswordResetTokenEntity;
import com.example.insurance.domain.user.model.User;

public interface PasswordResetTokenrepository extends JpaRepository<PasswordResetTokenEntity, Long> {

    Optional<PasswordResetTokenEntity> findByToken(String token);

    void deleteByUser(User user);

    void deleteByExpiryDateBefore(Instant expiryDate);

}
