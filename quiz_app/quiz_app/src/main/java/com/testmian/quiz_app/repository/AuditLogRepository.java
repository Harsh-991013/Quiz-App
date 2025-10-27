package com.testmian.quiz_app.repository;

import com.testmian.quiz_app.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Integer> {
    List<AuditLog> findByUser_UserIdOrderByCreatedAtDesc(Integer userId);
    List<AuditLog> findByEntityTypeAndEntityIdOrderByCreatedAtDesc(String entityType, Integer entityId);
    List<AuditLog> findByActionTypeOrderByCreatedAtDesc(String actionType);
    List<AuditLog> findByModuleNameOrderByCreatedAtDesc(String moduleName);
    List<AuditLog> findBySeverityOrderByCreatedAtDesc(String severity);
    List<AuditLog> findByCreatedAtBetweenOrderByCreatedAtDesc(java.time.LocalDateTime startDate, java.time.LocalDateTime endDate);
}