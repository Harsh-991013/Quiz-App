package com.testmian.quiz_app.repository;

import com.testmian.quiz_app.entity.PolicyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PolicyStatusRepository extends JpaRepository<PolicyStatus, Integer> {
    Optional<PolicyStatus> findByStatusKey(String statusKey);
}