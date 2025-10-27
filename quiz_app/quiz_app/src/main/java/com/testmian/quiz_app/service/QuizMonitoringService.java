package com.testmian.quiz_app.service;

import com.testmian.quiz_app.entity.*;
import com.testmian.quiz_app.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuizMonitoringService {

    private final QuizViolationRepository quizViolationRepository;
    private final QuizWarningRepository quizWarningRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final PolicyViolationTypeRepository policyViolationTypeRepository;
    private final ViolationActionRepository violationActionRepository;
    private final QuizPolicyRepository quizPolicyRepository;
    private final AutoSubmitReasonRepository autoSubmitReasonRepository;
    private final QuizAssignmentRepository quizAssignmentRepository;
    private final ScoringService scoringService;
    private final NotificationService notificationService;
    private final AssignmentStatusRepository assignmentStatusRepository;

    @Transactional
    public void recordViolation(Integer attemptId, Integer candidateId, String violationKey,
                              String description, String ipAddress, String deviceInfo) {
        // Find attempt and candidate
        QuizAttempt attempt = quizAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new RuntimeException("Attempt not found"));

        User candidate = attempt.getAssignment().getCandidate();
        if (!candidate.getUserId().equals(candidateId)) {
            throw new RuntimeException("Invalid candidate for attempt");
        }

        // Find violation type
        PolicyViolationType violationType = policyViolationTypeRepository.findByViolationKey(violationKey);
        if (violationType == null) {
            // Create default violation type if not found
            violationType = new PolicyViolationType();
            violationType.setViolationKey(violationKey);
            violationType.setDisplayName(violationKey.replace("_", " "));
            violationType.setDescription(description);
            violationType = policyViolationTypeRepository.save(violationType);
        }

        // Find default action (Warning)
        ViolationAction action = violationActionRepository.findByActionKey("WARNING");
        if (action == null) {
            action = new ViolationAction();
            action.setActionKey("WARNING");
            action.setDisplayName("Warning");
            action = violationActionRepository.save(action);
        }

        // Create violation record
        QuizViolation violation = new QuizViolation();
        violation.setAttempt(attempt);
        violation.setCandidate(candidate);
        violation.setViolationType(violationType);
        violation.setViolationDescription(description);
        violation.setIpAddress(ipAddress);
        violation.setDeviceInfo(deviceInfo);
        violation.setIsCritical(violationType.getSeverity() != null &&
                               "CRITICAL".equals(violationType.getSeverity().getSeverityKey()));
        violation.setHandledAction(action);
        violation.setTimestamp(LocalDateTime.now());

        quizViolationRepository.save(violation);

        // Issue warning
        issueWarning(attempt, violation, candidate);

        // Check if auto-submit threshold reached
        checkAutoSubmitThreshold(attempt, candidate);
    }

    private void issueWarning(QuizAttempt attempt, QuizViolation violation, User candidate) {
        // Count existing warnings for this attempt
        List<QuizWarning> existingWarnings = quizWarningRepository.findByAttempt_AttemptId(attempt.getAttemptId());
        int warningNumber = existingWarnings.size() + 1;

        // Get quiz policy
        QuizPolicy policy = quizPolicyRepository.findByQuiz_QuizId(attempt.getAssignment().getQuiz().getQuizId());
        boolean isFinalWarning = policy != null && warningNumber >= policy.getMaxWarningsBeforeAutoSubmit();

        // Customize warning message based on violation severity
        String severityMessage = "";
        if (violation.getViolationType().getSeverity() != null) {
            String severityKey = violation.getViolationType().getSeverity().getSeverityKey();
            switch (severityKey) {
                case "CRITICAL":
                    severityMessage = " [CRITICAL VIOLATION - IMMEDIATE ACTION REQUIRED]";
                    break;
                case "HIGH":
                    severityMessage = " [HIGH SEVERITY]";
                    break;
                case "MEDIUM":
                    severityMessage = " [MODERATE SEVERITY]";
                    break;
                case "LOW":
                    severityMessage = " [LOW SEVERITY]";
                    break;
            }
        }

        // Create warning
        QuizWarning warning = new QuizWarning();
        warning.setAttempt(attempt);
        warning.setViolation(violation);
        warning.setCandidate(candidate);
        warning.setMessage("Warning " + warningNumber + ": " + violation.getViolationDescription() + severityMessage);
        warning.setWarningNumber(warningNumber);
        warning.setIsFinalWarning(isFinalWarning);
        warning.setTimestamp(LocalDateTime.now());

        quizWarningRepository.save(warning);

        // Send notification to candidate with severity information
        String notificationMessage = "Quiz Policy Violation Warning" + severityMessage + ": " + violation.getViolationDescription();
        notificationService.sendQuizStartNotification(candidate.getUserId(), notificationMessage);
    }

    private void checkAutoSubmitThreshold(QuizAttempt attempt, User candidate) {
        QuizPolicy policy = quizPolicyRepository.findByQuiz_QuizId(attempt.getAssignment().getQuiz().getQuizId());
        if (policy == null) return;

        List<QuizWarning> warnings = quizWarningRepository.findByAttempt_AttemptId(attempt.getAttemptId());
        List<QuizViolation> violations = quizViolationRepository.findByAttempt_AttemptId(attempt.getAttemptId());

        // Check warning threshold
        boolean warningThresholdReached = warnings.size() >= policy.getMaxWarningsBeforeAutoSubmit();

        // Check for critical violations (immediate auto-submit)
        boolean hasCriticalViolation = violations.stream()
                .anyMatch(violation -> Boolean.TRUE.equals(violation.getIsCritical()));

        if (warningThresholdReached || hasCriticalViolation) {
            String reasonKey = hasCriticalViolation ? "CRITICAL_VIOLATION" : "MAX_VIOLATION_REACHED";
            // Auto-submit the quiz
            autoSubmitQuiz(attempt, candidate, reasonKey);
        }
    }

    private void autoSubmitQuiz(QuizAttempt attempt, User candidate, String reasonKey) {
        // Update attempt status
        attempt.setEndTime(LocalDateTime.now());
        attempt.setAutoSubmitted(true);

        // Find auto-submit reason
        AutoSubmitReason reason = autoSubmitReasonRepository.findByReasonKey(reasonKey);
        if (reason != null) {
            attempt.setAutoSubmitReason(reason);
        }

        // Calculate and save score
        scoringService.calculateAndSaveScores(attempt.getAttemptId());

        quizAttemptRepository.save(attempt);

        // Update assignment status
        QuizAssignment assignment = attempt.getAssignment();
        // Find auto-submitted status
        AssignmentStatus autoSubmittedStatus = assignmentStatusRepository.findByStatusKey("Auto_Submitted")
            .orElseThrow(() -> new RuntimeException("Status not found"));
        assignment.setAssignmentStatus(autoSubmittedStatus);
        quizAssignmentRepository.save(assignment);

        // Send notification
        notificationService.sendQuizAutoSubmitNotification(candidate.getUserId(),
            attempt.getAssignment().getQuiz().getTitle(),
            attempt.getTotalScore() != null ? attempt.getTotalScore() : 0.0f);
    }

    public List<QuizViolation> getViolationsForAttempt(Integer attemptId) {
        return quizViolationRepository.findByAttempt_AttemptId(attemptId);
    }

    public List<QuizWarning> getWarningsForAttempt(Integer attemptId) {
        return quizWarningRepository.findByAttempt_AttemptId(attemptId);
    }

    public int getViolationCountForAttempt(Integer attemptId) {
        return (int) quizViolationRepository.countByAttempt_AttemptId(attemptId);
    }

    public int getWarningCountForAttempt(Integer attemptId) {
        return (int) quizWarningRepository.countByAttempt_AttemptId(attemptId);
    }

    public int getCriticalViolationCountForAttempt(Integer attemptId) {
        return (int) quizViolationRepository.findByAttempt_AttemptId(attemptId).stream()
                .filter(violation -> Boolean.TRUE.equals(violation.getIsCritical()))
                .count();
    }

    public List<QuizViolation> getCriticalViolationsForAttempt(Integer attemptId) {
        return quizViolationRepository.findByAttempt_AttemptId(attemptId).stream()
                .filter(violation -> Boolean.TRUE.equals(violation.getIsCritical()))
                .toList();
    }
}