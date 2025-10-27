package com.example.quiz.service;

import com.example.quiz.entity.Question;
import com.example.quiz.entity.QuestionOption;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class QuizRandomizationService {

    public List<Question> randomizeQuestions(List<Question> questions) {
        if (questions == null || questions.isEmpty()) {
            return questions;
        }

        List<Question> shuffledQuestions = new ArrayList<>(questions);
        Collections.shuffle(shuffledQuestions);

        log.info("Randomized {} questions", questions.size());
        return shuffledQuestions;
    }

    public List<QuestionOption> randomizeOptions(List<QuestionOption> options) {
        if (options == null || options.isEmpty()) {
            return options;
        }

        List<QuestionOption> shuffledOptions = new ArrayList<>(options);
        Collections.shuffle(shuffledOptions);

        log.info("Randomized {} options", options.size());
        return shuffledOptions;
    }

    public List<Question> selectRandomQuestions(List<Question> questions, int count) {
        if (questions == null || questions.isEmpty() || count <= 0) {
            return new ArrayList<>();
        }

        if (count >= questions.size()) {
            return new ArrayList<>(questions);
        }

        List<Question> shuffled = new ArrayList<>(questions);
        Collections.shuffle(shuffled);

        List<Question> selected = shuffled.subList(0, count);
        log.info("Selected {} random questions out of {}", count, questions.size());

        return selected;
    }

    public List<Question> randomizeQuestionsByDifficulty(List<Question> questions) {
        if (questions == null || questions.isEmpty()) {
            return questions;
        }

        // Group questions by difficulty
        var groupedByDifficulty = questions.stream()
                .collect(Collectors.groupingBy(q -> q.getDifficulty().getDifficultyKey()));

        List<Question> randomizedQuestions = new ArrayList<>();

        // Randomize within each difficulty group and combine
        for (var entry : groupedByDifficulty.entrySet()) {
            List<Question> difficultyQuestions = entry.getValue();
            Collections.shuffle(difficultyQuestions);
            randomizedQuestions.addAll(difficultyQuestions);
        }

        log.info("Randomized questions by difficulty levels");
        return randomizedQuestions;
    }
}