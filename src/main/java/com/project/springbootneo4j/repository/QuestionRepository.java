package com.project.springbootneo4j.repository;

import com.project.springbootneo4j.model.Entity;
import com.project.springbootneo4j.model.Station;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestionRepository extends Neo4jRepository<Station, Long> {


    @Query("{name}")
    List<Entity> searchAnswer(@Param("name") String name);


}
