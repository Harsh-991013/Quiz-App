package com.testmian.quiz_app.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.testmian.quiz_app.entity.InviteStatus;

public interface InviteStatusRepository extends JpaRepository<InviteStatus, Long> {
    Optional<InviteStatus> findByStatusKey(String statusKey);
}
