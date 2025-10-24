package com.quiz.accesscontrol.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.quiz.accesscontrol.entity.MagicLinkPurpose;

public interface MagicLinkPurposeRepository extends JpaRepository<MagicLinkPurpose, Long> {
    Optional<MagicLinkPurpose> findByPurposeKey(String purposeKey);
}
