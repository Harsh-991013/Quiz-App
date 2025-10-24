package com.quiz.accesscontrol.repository;

import com.quiz.accesscontrol.entity.MagicLink;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MagicLinkRepository extends JpaRepository<MagicLink, Long> {
    Optional<MagicLink> findByTokenHashAndUsedFalse(String tokenHash);
}

