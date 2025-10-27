package com.example.quiz.repository;

import com.example.quiz.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    @Query("SELECT al FROM AuditLog al WHERE al.user.userId = :userId ORDER BY al.createdAt DESC")
    List<AuditLog> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);

    @Query("SELECT al FROM AuditLog al WHERE al.actionType = :actionType ORDER BY al.createdAt DESC")
    List<AuditLog> findByActionTypeOrderByCreatedAtDesc(@Param("actionType") String actionType);

    @Query("SELECT al FROM AuditLog al WHERE al.entityType = :entityType AND al.entityId = :entityId ORDER BY al.createdAt DESC")
    List<AuditLog> findByEntityTypeAndEntityIdOrderByCreatedAtDesc(@Param("entityType") String entityType, @Param("entityId") Long entityId);

    @Query("SELECT al FROM AuditLog al WHERE al.createdAt BETWEEN :startDate AND :endDate ORDER BY al.createdAt DESC")
    List<AuditLog> findByCreatedAtBetweenOrderByCreatedAtDesc(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT al FROM AuditLog al WHERE al.role = :role ORDER BY al.createdAt DESC")
    List<AuditLog> findByRoleOrderByCreatedAtDesc(@Param("role") String role);
}