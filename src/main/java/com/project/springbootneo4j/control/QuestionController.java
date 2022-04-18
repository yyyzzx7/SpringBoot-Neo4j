package com.project.springbootneo4j.control;

import com.project.springbootneo4j.model.Entity;
import com.project.springbootneo4j.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/kgqa")
public class QuestionController {

    @Autowired
    QuestionService questionService;

    @RequestMapping(method = RequestMethod.GET, path = "/query")
    public List<Entity> query(@Param("question") String question) throws Exception {
        return questionService.query(question);
    }
}
