package com.quiz.accesscontrol.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.quiz.accesscontrol.entity.UserStatus;

public interface UserStatusRepository extends JpaRepository<UserStatus, Long> {
    Optional<UserStatus> findByStatusKey(String statusKey);
}
