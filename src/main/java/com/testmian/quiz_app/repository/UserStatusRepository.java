package com.testmian.quiz_app.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.testmian.quiz_app.entity.UserStatus;

public interface UserStatusRepository extends JpaRepository<UserStatus, Long> {
    Optional<UserStatus> findByStatusKey(String statusKey);
}
