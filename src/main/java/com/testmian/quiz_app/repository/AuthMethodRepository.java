package com.testmian.quiz_app.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.testmian.quiz_app.entity.AuthMethod;

public interface AuthMethodRepository extends JpaRepository<AuthMethod, Long> {
    Optional<AuthMethod> findByMethodKey(String methodKey);
}
