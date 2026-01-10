package com.example.insurance.domain.emailService.verificationTokenRepo;

import com.example.insurance.domain.emailService.emailVerificationToken.VerificationToken;
import com.example.insurance.domain.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    Optional<VerificationToken> findByToken(String token);

    Optional<VerificationToken> findByUser(User user);

    void deleteByUser(User user);

    @Modifying
    @Query("DELETE FROM VerificationToken t WHERE t.expiryDate <= ?1")
    int deleteAllExpiredSince(LocalDateTime now);
}