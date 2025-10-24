package com.quiz.accesscontrol.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.quiz.accesscontrol.entity.AuthMethod;

public interface AuthMethodRepository extends JpaRepository<AuthMethod, Long> {
    Optional<AuthMethod> findByMethodKey(String methodKey);
}
