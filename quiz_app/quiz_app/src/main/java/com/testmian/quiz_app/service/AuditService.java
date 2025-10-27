package com.testmian.quiz_app.service;

import com.testmian.quiz_app.entity.AuditLog;
import com.testmian.quiz_app.entity.User;
import com.testmian.quiz_app.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    public void logAction(User user, String actionType, String entityType, Integer entityId,
                         String description, String moduleName, String severity,
                         String ipAddress, String deviceInfo, String sessionId,
                         String oldValue, String newValue) {

        AuditLog auditLog = new AuditLog();
        auditLog.setUser(user);
        auditLog.setRole(user != null ? "Candidate" : "System"); // Default role
        auditLog.setActionType(actionType);
        auditLog.setEntityType(entityType);
        auditLog.setEntityId(entityId);
        auditLog.setDescription(description);
        auditLog.setModuleName(moduleName);
        auditLog.setSeverity(severity);
        auditLog.setIpAddress(ipAddress);
        auditLog.setDeviceInfo(deviceInfo);
        auditLog.setSessionId(sessionId);
        auditLog.setOldValue(oldValue);
        auditLog.setNewValue(newValue);
        auditLog.setCreatedAt(LocalDateTime.now());

        auditLogRepository.save(auditLog);
    }

    // Convenience methods for common audit actions
    public void logQuizAction(User user, String action, Integer attemptId, String description,
                             String ipAddress, String deviceInfo, String sessionId) {
        logAction(user, action, "QuizAttempt", attemptId, description,
                 "QUIZ_MODULE", "INFO", ipAddress, deviceInfo, sessionId, null, null);
    }

    public void logSecurityEvent(User user, String eventType, String description,
                                String ipAddress, String deviceInfo, String sessionId) {
        logAction(user, eventType, "Security", null, description,
                 "SECURITY_MODULE", "WARNING", ipAddress, deviceInfo, sessionId, null, null);
    }

    public void logViolation(User user, String violationType, Integer attemptId, String description,
                            String ipAddress, String deviceInfo, String sessionId) {
        logAction(user, violationType, "QuizAttempt", attemptId, description,
                 "VIOLATION_MODULE", "CRITICAL", ipAddress, deviceInfo, sessionId, null, null);
    }

    public void logScoreChange(User user, Integer scoreId, String oldScore, String newScore,
                              String ipAddress, String deviceInfo, String sessionId) {
        logAction(user, "SCORE_UPDATE", "Score", scoreId,
                 "Score updated from " + oldScore + " to " + newScore,
                 "SCORING_MODULE", "INFO", ipAddress, deviceInfo, sessionId, oldScore, newScore);
    }

    // Query methods
    public List<AuditLog> getUserAuditLogs(Integer userId) {
        return auditLogRepository.findByUser_UserIdOrderByCreatedAtDesc(userId);
    }

    public List<AuditLog> getEntityAuditLogs(String entityType, Integer entityId) {
        return auditLogRepository.findByEntityTypeAndEntityIdOrderByCreatedAtDesc(entityType, entityId);
    }

    public List<AuditLog> getActionAuditLogs(String actionType) {
        return auditLogRepository.findByActionTypeOrderByCreatedAtDesc(actionType);
    }

    public List<AuditLog> getModuleAuditLogs(String moduleName) {
        return auditLogRepository.findByModuleNameOrderByCreatedAtDesc(moduleName);
    }

    public List<AuditLog> getSeverityAuditLogs(String severity) {
        return auditLogRepository.findBySeverityOrderByCreatedAtDesc(severity);
    }

    public List<AuditLog> getAuditLogsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return auditLogRepository.findByCreatedAtBetweenOrderByCreatedAtDesc(startDate, endDate);
    }
}