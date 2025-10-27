package com.example.quiz.service;

import com.example.quiz.entity.*;
import com.example.quiz.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuizService {

    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final QuizQuestionRepository quizQuestionRepository;
    private final QuizAssignmentRepository quizAssignmentRepository;
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;

    @Transactional
    public Quiz createQuiz(String title, String description, LocalDateTime startTime,
                          LocalDateTime endTime, Integer durationMinutes, Long difficultyId,
                          List<Long> questionIds, User createdBy) {

        // Validate question count
        if (questionIds.size() > 100) {
            throw new IllegalArgumentException("Maximum 100 questions allowed per quiz");
        }

        // Validate questions exist and are active
        List<Question> questions = questionRepository.findAllActiveByIds(questionIds);
        if (questions.size() != questionIds.size()) {
            throw new IllegalArgumentException("Some questions are not available or inactive");
        }

        // Create quiz
        Quiz quiz = new Quiz();
        quiz.setTitle(title);
        quiz.setDescription(description);
        quiz.setStartTime(startTime);
        quiz.setEndTime(endTime);
        quiz.setDurationMinutes(durationMinutes);
        quiz.setCreatedBy(createdBy);

        if (difficultyId != null) {
            DifficultyLevel difficulty = new DifficultyLevel();
            difficulty.setDifficultyId(difficultyId);
            quiz.setDifficulty(difficulty);
        }

        Quiz savedQuiz = quizRepository.save(quiz);

        // Add questions to quiz
        for (int i = 0; i < questions.size(); i++) {
            QuizQuestion quizQuestion = new QuizQuestion();
            quizQuestion.setQuiz(savedQuiz);
            quizQuestion.setQuestion(questions.get(i));
            quizQuestion.setQuestionOrder(i + 1);
            quizQuestionRepository.save(quizQuestion);
        }

        // Log audit
        auditLogService.logAction(createdBy, "CREATE", "Quiz", savedQuiz.getQuizId(),
                "Created quiz: " + title);

        return savedQuiz;
    }

    @Transactional
    public Quiz updateQuiz(Long quizId, String title, String description, LocalDateTime startTime,
                          LocalDateTime endTime, Integer durationMinutes, User updatedBy) {

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new IllegalArgumentException("Quiz not found"));

        if (quiz.isDeleted()) {
            throw new IllegalArgumentException("Cannot update deleted quiz");
        }

        quiz.setTitle(title);
        quiz.setDescription(description);
        quiz.setStartTime(startTime);
        quiz.setEndTime(endTime);
        quiz.setDurationMinutes(durationMinutes);
        quiz.setUpdatedBy(updatedBy);

        Quiz savedQuiz = quizRepository.save(quiz);

        // Log audit
        auditLogService.logAction(updatedBy, "UPDATE", "Quiz", quizId, "Updated quiz: " + title);

        return savedQuiz;
    }

    @Transactional
    public void softDeleteQuiz(Long quizId, User deletedBy) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new IllegalArgumentException("Quiz not found"));

        if (quiz.isDeleted()) {
            throw new IllegalArgumentException("Quiz already deleted");
        }

        quiz.markAsDeleted(deletedBy);
        quizRepository.save(quiz);

        // Log audit
        auditLogService.logAction(deletedBy, "SOFT_DELETE", "Quiz", quizId, "Soft deleted quiz: " + quiz.getTitle());
    }

    @Transactional
    public void restoreQuiz(Long quizId, User restoredBy) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new IllegalArgumentException("Quiz not found"));

        if (!quiz.isDeleted()) {
            throw new IllegalArgumentException("Quiz is not deleted");
        }

        quiz.restore();
        quiz.setUpdatedBy(restoredBy);
        quizRepository.save(quiz);

        // Log audit
        auditLogService.logAction(restoredBy, "RESTORE", "Quiz", quizId, "Restored quiz: " + quiz.getTitle());
    }

    public List<Quiz> getAllActiveQuizzes() {
        return quizRepository.findAllActive();
    }

    public List<Quiz> getQuizzesByCreator(Long creatorId) {
        return quizRepository.findAllActiveByCreator(creatorId);
    }

    public Quiz getQuizById(Long quizId) {
        return quizRepository.findById(quizId)
                .filter(quiz -> !quiz.isDeleted())
                .orElseThrow(() -> new IllegalArgumentException("Quiz not found"));
    }

    @Transactional
    public String assignQuizToCandidate(Long quizId, Long candidateId, User assignedBy) {
        try {
            Quiz quiz = getQuizById(quizId);
            User candidate = userRepository.findById(candidateId)
                    .orElseThrow(() -> new IllegalArgumentException("Candidate not found"));

            // Check if already assigned (allow re-assignment if previous was deleted)
            Optional<QuizAssignment> existing = quizAssignmentRepository
                    .findLatestActiveAssignmentForQuizAndCandidate(quizId, candidateId);
            if (existing.isPresent()) {
                throw new IllegalArgumentException("Quiz already assigned to this candidate");
            }

            // Generate unique link
            String uniqueLink = UUID.randomUUID().toString();

            // Calculate expiration
            LocalDateTime expiresAt = quiz.getEndTime() != null ? quiz.getEndTime() :
                    LocalDateTime.now().plusDays(7);

            QuizAssignment assignment = new QuizAssignment();
            assignment.setQuiz(quiz);
            assignment.setCandidate(candidate);
            assignment.setUniqueLink(uniqueLink);
            assignment.setExpiresAt(expiresAt);
            assignment.setAssignedBy(assignedBy);

            QuizAssignment saved = quizAssignmentRepository.save(assignment);

            // Log audit (only if assignedBy is not null)
            if (assignedBy != null) {
                auditLogService.logAction(assignedBy, "ASSIGN", "QuizAssignment", saved.getAssignmentId(),
                        "Assigned quiz " + quiz.getTitle() + " to candidate " + candidate.getEmail());
            }

            return "Quiz assigned successfully. Unique link: " + uniqueLink;
        } catch (Exception e) {
            log.error("Error assigning quiz to candidate", e);
            throw e;
        }
    }

    public List<QuizAssignment> getAssignmentsByQuiz(Long quizId) {
        return quizAssignmentRepository.findAllActiveByQuizId(quizId);
    }

    public List<QuizAssignment> getAssignmentsByCandidate(Long candidateId) {
        return quizAssignmentRepository.findAllActiveByCandidateId(candidateId);
    }

    public QuizAssignment getAssignmentByUniqueLink(String uniqueLink) {
        return quizAssignmentRepository.findByUniqueLink(uniqueLink)
                .filter(assignment -> assignment.getDeletedAt() == null)
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired quiz link"));
    }
}