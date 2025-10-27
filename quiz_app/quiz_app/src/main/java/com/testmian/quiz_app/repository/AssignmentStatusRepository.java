package com.testmian.quiz_app.repository;

import com.testmian.quiz_app.entity.AssignmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AssignmentStatusRepository extends JpaRepository<AssignmentStatus, Integer> {
    Optional<AssignmentStatus> findByStatusKey(String statusKey);
}