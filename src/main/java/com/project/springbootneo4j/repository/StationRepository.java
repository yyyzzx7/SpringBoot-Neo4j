package com.project.springbootneo4j.repository;

import com.project.springbootneo4j.model.Station;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StationRepository extends Neo4jRepository<Station, Long> {

    // 1. {location}有哪些<{train_type}>站？
    @Query("MATCH (s:Station)-[:`所属省份`]->(p:Province) WHERE s.name contains {name} OR p.name contains {name} RETURN s")
    List<Station> getStationMethod1(@Param("name") String name);


}
