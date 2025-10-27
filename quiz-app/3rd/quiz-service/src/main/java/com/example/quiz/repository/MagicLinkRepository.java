package com.example.quiz.repository;

import com.example.quiz.entity.MagicLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MagicLinkRepository extends JpaRepository<MagicLink, Long> {

    Optional<MagicLink> findByTokenHash(String tokenHash);

    @Query("SELECT ml FROM MagicLink ml WHERE ml.user.userId = :userId AND ml.used = false AND ml.expiresAt > :now ORDER BY ml.createdAt DESC")
    List<MagicLink> findActiveByUserId(@Param("userId") Long userId, @Param("now") LocalDateTime now);

    @Query("SELECT ml FROM MagicLink ml WHERE ml.expiresAt < :now AND ml.used = false")
    List<MagicLink> findExpiredUnusedLinks(@Param("now") LocalDateTime now);

    @Query("SELECT ml FROM MagicLink ml WHERE ml.purpose = :purpose AND ml.user.userId = :userId ORDER BY ml.createdAt DESC")
    List<MagicLink> findByPurposeAndUserId(@Param("purpose") MagicLink.Purpose purpose, @Param("userId") Long userId);
}
