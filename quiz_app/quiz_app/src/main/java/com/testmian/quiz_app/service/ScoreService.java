package com.testmian.quiz_app.service;

import com.testmian.quiz_app.entity.Score;
import com.testmian.quiz_app.repository.ScoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScoreService {

    private final ScoreRepository scoreRepository;

    public List<Score> getScoresByAttemptId(Integer attemptId) {
        return scoreRepository.findByAttempt_AttemptId(attemptId);
    }

    public List<Score> getScoresByCandidateId(Integer candidateId) {
        return scoreRepository.findByCandidate_UserId(candidateId);
    }

    public List<Score> getScoresByQuizId(Integer quizId) {
        // This would need a custom query in the repository
        // For now, we'll filter from all scores
        return scoreRepository.findAll().stream()
                .filter(score -> score.getAttempt().getAssignment().getQuiz().getQuizId().equals(quizId))
                .collect(Collectors.toList());
    }

}