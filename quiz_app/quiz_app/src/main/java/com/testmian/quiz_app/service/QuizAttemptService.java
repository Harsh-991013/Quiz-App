package com.testmian.quiz_app.service;

import com.testmian.quiz_app.entity.*;
import com.testmian.quiz_app.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

@Service
public class QuizAttemptService {

    private final QuizAssignmentRepository quizAssignmentRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final QuizQuestionRepository quizQuestionRepository;
    private final QuestionOptionRepository questionOptionRepository;
    private final AttemptAnswerRepository attemptAnswerRepository;
    private final AuditLogRepository auditLogRepository;
    private final ScoreRepository scoreRepository;
    private final NotificationService notificationService;
    private final AuditService auditService;
    private final AssignmentStatusRepository assignmentStatusRepository;
    private final PolicyStatusRepository policyStatusRepository;

    // Constructor injection
    public QuizAttemptService(QuizAssignmentRepository quizAssignmentRepository,
                            QuizAttemptRepository quizAttemptRepository,
                            QuizQuestionRepository quizQuestionRepository,
                            QuestionOptionRepository questionOptionRepository,
                            AttemptAnswerRepository attemptAnswerRepository,
                            AuditLogRepository auditLogRepository,
                            ScoreRepository scoreRepository,
                            NotificationService notificationService,
                            AuditService auditService,
                            AssignmentStatusRepository assignmentStatusRepository,
                            PolicyStatusRepository policyStatusRepository) {
        this.quizAssignmentRepository = quizAssignmentRepository;
        this.quizAttemptRepository = quizAttemptRepository;
        this.quizQuestionRepository = quizQuestionRepository;
        this.questionOptionRepository = questionOptionRepository;
        this.attemptAnswerRepository = attemptAnswerRepository;
        this.auditLogRepository = auditLogRepository;
        this.scoreRepository = scoreRepository;
        this.notificationService = notificationService;
        this.auditService = auditService;
        this.assignmentStatusRepository = assignmentStatusRepository;
        this.policyStatusRepository = policyStatusRepository;

        System.out.println("QUIZ ATTEMPT SERVICE STARTED - NotificationService injected: " + (notificationService != null));
    }

    @Transactional
    public QuizAttempt startQuizAttempt(String uniqueLink, Integer candidateId, Integer sessionId) {
        System.out.println("STARTING QUIZ ATTEMPT - uniqueLink: " + uniqueLink + ", candidateId: " + candidateId);

        // Find assignment by unique link
        Optional<QuizAssignment> assignmentOpt = quizAssignmentRepository.findByUniqueLink(uniqueLink);
        if (assignmentOpt.isEmpty()) {
            throw new RuntimeException("Invalid quiz link");
        }

        QuizAssignment assignment = assignmentOpt.get();

        // Validate candidate
        if (!assignment.getCandidate().getUserId().equals(candidateId)) {
            throw new RuntimeException("Unauthorized access to quiz");
        }

        // Check if assignment is expired
        if (assignment.getExpiresAt() != null && assignment.getExpiresAt().isBefore(LocalDateTime.now())) {
            // Find expired status
            AssignmentStatus expiredStatus = assignmentStatusRepository.findByStatusKey("Expired")
                .orElseThrow(() -> new RuntimeException("Status not found"));
            assignment.setAssignmentStatus(expiredStatus);
            quizAssignmentRepository.save(assignment);
            throw new RuntimeException("Quiz link has expired");
        }

        // Check if assignment is already started or completed
        if (assignment.getAssignmentStatus() != null && !"Assigned".equals(assignment.getAssignmentStatus().getStatusKey())) {
            throw new RuntimeException("Quiz has already been " + assignment.getAssignmentStatus().getStatusKey().toLowerCase());
        }

        // Check if attempt already exists
        Optional<QuizAttempt> existingAttempt = quizAttemptRepository.findByAssignment_AssignmentId(assignment.getAssignmentId());
        QuizAttempt attempt;

        if (existingAttempt.isPresent()) {
            attempt = existingAttempt.get();
            System.out.println("Using existing quiz attempt: " + attempt.getAttemptId());
        } else {
            // Create new attempt
            attempt = new QuizAttempt();
            attempt.setAssignment(assignment);

            // Set session if provided (for future session tracking)
            if (sessionId != null) {
                // Note: Session entity was removed from the simplified model
                // This is kept for future implementation when session tracking is needed
                // For now, sessionId is not stored but the parameter is accepted
 //            Session session = sessionRepository.findById(sessionId)
//                    .orElseThrow(() -> new RuntimeException("Invalid session"));
//            attempt.setSession(session);
            }

            attempt.setStartTime(LocalDateTime.now());
            attempt.setAutoSubmitted(false);
            attempt.setCreatedAt(LocalDateTime.now());

            // Set default policy status (Active)
            PolicyStatus activeStatus = policyStatusRepository.findByStatusKey("Active")
                .orElseThrow(() -> new RuntimeException("Policy status not found"));
            attempt.setPolicyStatus(activeStatus);

            attempt = quizAttemptRepository.save(attempt);

            // Update assignment status only for new attempts
            AssignmentStatus startedStatus = assignmentStatusRepository.findByStatusKey("Started")
                .orElseThrow(() -> new RuntimeException("Status not found"));
            assignment.setAssignmentStatus(startedStatus);
            quizAssignmentRepository.save(assignment);

            // Log the action for new attempts
            auditService.logQuizAction(assignment.getCandidate(), "QUIZ_START", attempt.getAttemptId(),
                "Started quiz attempt", null, null, null);
        }

        // Send quiz start notification (for both new and existing attempts)
        try {
            System.out.println("QuizAttemptService: Sending quiz start notification for candidate: " + candidateId);
            notificationService.sendQuizStartNotification(candidateId, assignment.getQuiz().getTitle());
            System.out.println("QuizAttemptService: Quiz start notification sent successfully");
        } catch (Exception e) {
            // Log error but don't fail the quiz start
            System.err.println("QuizAttemptService: Failed to send quiz start notification: " + e.getMessage());
        }

        return attempt;
    }

    public List<QuizQuestion> getQuizQuestions(Integer attemptId, Integer candidateId) {
        // Validate attempt belongs to candidate
        Optional<QuizAttempt> attemptOpt = quizAttemptRepository.findByAttemptIdAndAssignment_Candidate_UserId(attemptId, candidateId);
        if (attemptOpt.isEmpty()) {
            System.out.println("DEBUG: No attempt found for attemptId=" + attemptId + ", candidateId=" + candidateId);
            throw new RuntimeException("Invalid attempt or unauthorized access");
        }

        QuizAttempt attempt = attemptOpt.get();
        System.out.println("DEBUG: Found attempt for candidate " + attempt.getAssignment().getCandidate().getUserId());

        List<QuizQuestion> questions = quizQuestionRepository.findQuestionsByQuizId(attempt.getAssignment().getQuiz().getQuizId());
        System.out.println("DEBUG: Found " + questions.size() + " questions for quiz " + attempt.getAssignment().getQuiz().getQuizId());

        // Randomize question order for this attempt
        Collections.shuffle(questions, new Random(attemptId)); // Use attemptId as seed for consistent randomization per attempt

        return questions;
    }

    public QuizQuestion getQuestionByIndex(Integer attemptId, Integer candidateId, Integer questionIndex) {
        List<QuizQuestion> questions = getQuizQuestions(attemptId, candidateId);

        if (questionIndex < 0 || questionIndex >= questions.size()) {
            throw new RuntimeException("Invalid question index");
        }

        return questions.get(questionIndex);
    }

    public List<Boolean> getQuestionStatus(Integer attemptId, Integer candidateId) {
        List<QuizQuestion> questions = getQuizQuestions(attemptId, candidateId);
        List<Boolean> status = new java.util.ArrayList<>();

        for (QuizQuestion qq : questions) {
            Optional<AttemptAnswer> answer = attemptAnswerRepository.findByAttempt_AttemptIdAndQuestion_QuestionId(attemptId, qq.getQuestion().getQuestionId());
            status.add(answer.isPresent());
        }

        return status;
    }

    @Transactional
    public AttemptAnswer submitAnswer(Integer attemptId, Integer candidateId, Integer questionId, Integer selectedOptionId) {
        // Validate attempt
        Optional<QuizAttempt> attemptOpt = quizAttemptRepository.findByAttemptIdAndAssignment_Candidate_UserId(attemptId, candidateId);
        if (attemptOpt.isEmpty()) {
            throw new RuntimeException("Invalid attempt or unauthorized access");
        }

        QuizAttempt attempt = attemptOpt.get();

        // Check if quiz is still active
        if (attempt.getEndTime() != null) {
            throw new RuntimeException("Quiz has already ended");
        }

        // Find or create answer
        Optional<AttemptAnswer> existingAnswer = attemptAnswerRepository.findByAttempt_AttemptIdAndQuestion_QuestionId(attemptId, questionId);
        AttemptAnswer answer;

        if (existingAnswer.isPresent()) {
            answer = existingAnswer.get();
        } else {
            answer = new AttemptAnswer();
            answer.setAttempt(attempt);
            answer.setQuestion(new Question());
            answer.getQuestion().setQuestionId(questionId);
        }

        // Set selected option
        if (selectedOptionId != null) {
            QuestionOption selectedOption = questionOptionRepository.findById(selectedOptionId)
                    .orElseThrow(() -> new RuntimeException("Invalid option selected"));
            answer.setSelectedOption(selectedOption);
            answer.setIsCorrect(selectedOption.getIsCorrect());
            answer.setMarksObtained(selectedOption.getIsCorrect() ? getQuestionMarks(attempt.getAssignment().getQuiz().getQuizId(), questionId) : 0.0f);
        }

        answer.setAnsweredAt(LocalDateTime.now());

        AttemptAnswer savedAnswer = attemptAnswerRepository.save(answer);

        // Log the action
        auditService.logQuizAction(attempt.getAssignment().getCandidate(), "ANSWER_SUBMIT", savedAnswer.getAnswerId(),
            "Submitted answer for question " + questionId, null, null, null);

        return savedAnswer;
    }

    @Transactional
    public QuizAttempt submitQuiz(Integer attemptId, Integer candidateId) {
        // Validate attempt
        Optional<QuizAttempt> attemptOpt = quizAttemptRepository.findByAttemptIdAndAssignment_Candidate_UserId(attemptId, candidateId);
        if (attemptOpt.isEmpty()) {
            throw new RuntimeException("Invalid attempt or unauthorized access");
        }

        QuizAttempt attempt = attemptOpt.get();

        // Check if already submitted
        if (attempt.getEndTime() != null) {
            return attempt;
        }

        // Set end time
        attempt.setEndTime(LocalDateTime.now());

        // Calculate total score
        List<AttemptAnswer> answers = attemptAnswerRepository.findByAttempt_AttemptId(attemptId);
        float totalScore = answers.stream()
                .filter(a -> a.getMarksObtained() != null)
                .map(AttemptAnswer::getMarksObtained)
                .reduce(0.0f, Float::sum);
        attempt.setTotalScore(totalScore);

        QuizAttempt savedAttempt = quizAttemptRepository.save(attempt);

        // Update assignment status
        QuizAssignment assignment = attempt.getAssignment();
        AssignmentStatus completedStatus = assignmentStatusRepository.findByStatusKey("Completed")
            .orElseThrow(() -> new RuntimeException("Status not found"));
        assignment.setAssignmentStatus(completedStatus);
        quizAssignmentRepository.save(assignment);

        // Send quiz completion notification
        try {
            List<Score> scores = scoreRepository.findByAttempt_AttemptId(attemptId);
            Integer correctAnswers = scores.isEmpty() ? 0 : scores.get(0).getCorrectAnswers();
            Integer totalQuestions = scores.isEmpty() ? 0 : scores.get(0).getTotalQuestions();

            notificationService.sendQuizCompletionNotification(candidateId, assignment.getQuiz().getTitle(), totalScore, correctAnswers, totalQuestions);
        } catch (Exception e) {
            // Log error but don't fail the quiz submission
            System.err.println("Failed to send quiz completion notification: " + e.getMessage());
        }

        // Log the action
        auditService.logQuizAction(assignment.getCandidate(), "QUIZ_SUBMIT", savedAttempt.getAttemptId(),
            "Submitted quiz with score: " + totalScore, null, null, null);

        return savedAttempt;
    }

    @Transactional
    public QuizAttempt autoSubmitQuiz(Integer attemptId) {
        Optional<QuizAttempt> attemptOpt = quizAttemptRepository.findById(attemptId);
        if (attemptOpt.isEmpty()) {
            throw new RuntimeException("Invalid attempt");
        }

        QuizAttempt attempt = attemptOpt.get();

        // Check if already submitted
        if (attempt.getEndTime() != null) {
            return attempt;
        }

        // Set end time and auto-submit flag
        attempt.setEndTime(LocalDateTime.now());
        attempt.setAutoSubmitted(true);

        // Calculate total score
        List<AttemptAnswer> answers = attemptAnswerRepository.findByAttempt_AttemptId(attemptId);
        float totalScore = answers.stream()
                .filter(a -> a.getMarksObtained() != null)
                .map(AttemptAnswer::getMarksObtained)
                .reduce(0.0f, Float::sum);
        attempt.setTotalScore(totalScore);

        QuizAttempt savedAttempt = quizAttemptRepository.save(attempt);

        // Update assignment status
        QuizAssignment assignment = attempt.getAssignment();
        AssignmentStatus autoSubmittedStatus = assignmentStatusRepository.findByStatusKey("Auto_Submitted")
            .orElseThrow(() -> new RuntimeException("Status not found"));
        assignment.setAssignmentStatus(autoSubmittedStatus);
        quizAssignmentRepository.save(assignment);

        // Send auto-submit notification
        try {
            notificationService.sendQuizAutoSubmitNotification(assignment.getCandidate().getUserId(), assignment.getQuiz().getTitle(), totalScore);
        } catch (Exception e) {
            // Log error but don't fail the auto-submission
            System.err.println("Failed to send quiz auto-submit notification: " + e.getMessage());
        }

        // Log the action
        auditService.logQuizAction(assignment.getCandidate(), "QUIZ_AUTO_SUBMIT", savedAttempt.getAttemptId(),
            "Auto-submitted quiz with score: " + totalScore, null, null, null);

        return savedAttempt;
    }

    private Float getQuestionMarks(Integer quizId, Integer questionId) {
        return quizQuestionRepository.findByQuiz_QuizIdOrderById(quizId).stream()
                .filter(qq -> qq.getQuestion().getQuestionId().equals(questionId))
                .findFirst()
                .map(QuizQuestion::getMarks)
                .orElse(1.0f);
    }

    public List<QuestionOption> getQuestionOptions(Integer questionId, Integer attemptId) {
        List<QuestionOption> options = questionOptionRepository.findByQuestion_QuestionIdOrderByOptionId(questionId);

        // Randomize option order for this attempt
        Collections.shuffle(options, new Random(attemptId * 31L + questionId)); // Use attemptId + questionId as seed

        return options;
    }

}