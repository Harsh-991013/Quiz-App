package com.testmian.quiz_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.testmian.quiz_app.entity.MagicLink;

import java.util.Optional;

public interface MagicLinkRepository extends JpaRepository<MagicLink, Long> {
    Optional<MagicLink> findByTokenHashAndUsedFalse(String tokenHash);
}

