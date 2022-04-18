package com.project.springbootneo4j;

import com.project.springbootneo4j.repository.StationRepository;
import com.project.springbootneo4j.model.Station;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

@SpringBootTest
class SpringBootNeo4jApplicationTests {

    @Autowired
    StationRepository stationRepository;

    @Test
    public void testCreate(){
        Optional<Station> byId = stationRepository.findById(1L);
        byId.orElse(null);

    }


}
