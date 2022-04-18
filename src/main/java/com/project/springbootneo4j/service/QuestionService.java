package com.project.springbootneo4j.service;

import com.project.springbootneo4j.model.Entity;

import java.util.List;

public interface QuestionService {
    List<Entity> query(String question) throws Exception;
}
