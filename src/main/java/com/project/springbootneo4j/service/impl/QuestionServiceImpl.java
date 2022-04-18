package com.project.springbootneo4j.service.impl;

import com.project.springbootneo4j.core.CoreProcessor;
import com.project.springbootneo4j.model.Entity;
import com.project.springbootneo4j.repository.QuestionRepository;
import com.project.springbootneo4j.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Primary
public class QuestionServiceImpl implements QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private CoreProcessor coreProcessor;

    @Override
    public List<Entity> query(String question) {
        String cypher = coreProcessor.getCypher(question);
        return questionRepository.searchAnswer(cypher);
    }
}
