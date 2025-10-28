package com.testmian.quiz_app.service;

import com.testmian.quiz_app.entity.QuestionType;
import com.testmian.quiz_app.repository.QuestionTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class QuestionTypeService {

    @Autowired
    private QuestionTypeRepository typeRepository;

    public List<QuestionType> getAll() {
        return typeRepository.findAll();
    }

    public QuestionType create(QuestionType type) {
        if (typeRepository.existsByTypeKey(type.getTypeKey())) {
            throw new RuntimeException("Type with this key already exists.");
        }
        return typeRepository.save(type);
    }

}
