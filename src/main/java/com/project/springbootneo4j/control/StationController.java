package com.project.springbootneo4j.control;

import com.project.springbootneo4j.repository.StationRepository;
import com.project.springbootneo4j.model.Station;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;


@RestController
@RequestMapping("/kgqa/station")
public class StationController {

    // @Autowired
    @Resource
    private StationRepository stationRepository;

    // 1. {location}有哪些<{train_type}>站？
    @RequestMapping(method = RequestMethod.GET, path = "/one")
    public List<Station> getStationMethod1(@RequestParam String name) {
        return stationRepository.getStationMethod1(name);
    }


//    @RequestMapping(method = RequestMethod.GET, path = "/test")
//    public String testProject(@RequestParam String name) {
//
//        String stationName = null;
//        if (name != null) {
//            stationName = name.trim();
//        }
//        return stationName;
//    }


}
