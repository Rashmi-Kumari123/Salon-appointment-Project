package com.sitare.authservice.repository;

import com.sitare.authservice.model.EmailVerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, String> {
    Optional<EmailVerificationToken> findByToken(String token);
    
    Optional<EmailVerificationToken> findByUserId(Long userId);
    
    @Modifying
    @Query("DELETE FROM EmailVerificationToken evt WHERE evt.expiresAt < ?1 OR evt.isUsed = true")
    void deleteExpiredTokens(LocalDateTime now);
}
