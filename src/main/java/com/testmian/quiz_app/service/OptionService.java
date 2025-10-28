package com.testmian.quiz_app.service;

import com.testmian.quiz_app.entity.Option;
import com.testmian.quiz_app.entity.Question;
import com.testmian.quiz_app.repository.OptionRepository;
import com.testmian.quiz_app.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OptionService {

    @Autowired
    private OptionRepository optionRepository;

    @Autowired
    private QuestionRepository questionRepository;

    // Get all active (non-deleted) options for a question
    public List<Option> getOptionsByQuestion(Integer questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found."));
        return optionRepository.findByQuestion(question)
                .stream()
                .filter(o -> o.getDeletedAt() == null)
                .toList();
    }

    // Create new option for a question
    public Option createOption(Integer questionId, Option option) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found."));

        option.setQuestion(question);
        option.setCreatedAt(LocalDateTime.now());   //  Ensure not null
        option.setUpdatedAt(null);
        option.setDeletedAt(null);

        return optionRepository.save(option);
    }

    // Update existing option
    public Option updateOption(Integer id, Option updated) {
        Option existing = optionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Option not found."));

        existing.setOptionText(updated.getOptionText());
        existing.setIsCorrect(updated.getIsCorrect());
        existing.setUpdatedAt(LocalDateTime.now());   // update timestamp

        return optionRepository.save(existing);
    }

    // Soft delete option
    public void deleteOption(Integer id) {
        Option option = optionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Option not found."));
        option.setDeletedAt(LocalDateTime.now());
        optionRepository.save(option);
    }

    // Restore soft-deleted option
    public Option restoreOption(Integer id) {
        Option option = optionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Option not found."));

        if (option.getDeletedAt() != null) {
            option.setDeletedAt(null);
            option.setUpdatedAt(LocalDateTime.now());  //  mark restoration time
            optionRepository.save(option);
        }
        return option;
    }
}
