package com.testmian.quiz_app.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.testmian.quiz_app.entity.MagicLinkPurpose;

public interface MagicLinkPurposeRepository extends JpaRepository<MagicLinkPurpose, Long> {
    Optional<MagicLinkPurpose> findByPurposeKey(String purposeKey);
}
