package com.quiz.accesscontrol.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.quiz.accesscontrol.entity.InviteStatus;

public interface InviteStatusRepository extends JpaRepository<InviteStatus, Long> {
    Optional<InviteStatus> findByStatusKey(String statusKey);
}
