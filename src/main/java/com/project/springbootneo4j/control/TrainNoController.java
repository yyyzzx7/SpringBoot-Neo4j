package com.project.springbootneo4j.control;

import com.project.springbootneo4j.repository.TrainNoRepository;
import com.project.springbootneo4j.model.TrainNo;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/kgqa/trainNo")
public class TrainNoController {

    @Resource
    private TrainNoRepository trainNoRepository;

    // 2. 途径{location/station}的<{train_type}>有哪些？
    @RequestMapping(method = RequestMethod.GET, path = "/q2")
    public List<TrainNo> getTrainNoMethod2(@Param("name") String name) {
        return trainNoRepository.getTrainNoMethod2(name);
    }

    // 3. 从{location/station}到{location/station}有什么<{train_type}>？
    @RequestMapping(method = RequestMethod.GET, path = "/q3")
    public List<TrainNo> getTrainNoMethod3(@Param("from") String from, @Param("to") String to){
        return trainNoRepository.getTrainNoMethod3(from, to);
    }

    // 4. 终点站是{location/station}的<{train_type}>有哪些？
    @RequestMapping(method = RequestMethod.GET, path = "/q4")
    public List<TrainNo> getTrainNoMethod4(@Param("name") String name){
        return trainNoRepository.getTrainNoMethod4(name);
    }

    // 5. 从{location/station}出发的<{train_type}>有哪些？
    @RequestMapping(method = RequestMethod.GET, path = "/q5")
    public List<TrainNo> getTrainNoMethod5(@Param("name") String name){
        return trainNoRepository.getTrainNoMethod5(name);
    }

    // 6. {time}从{location/station}到{location/station}有什么<{train_type}>？
    @RequestMapping(method = RequestMethod.GET, path = "/q6")
    public List<TrainNo> getTrainNoMethod6(@Param("time") String time, @Param("from") String from, @Param("to") String to){
        return trainNoRepository.getTrainNoMethod6(time, from, to);
    }

    // 7. 从{location/station}出发，{time}能到{location/station}的<{train_type}>有哪些？
    @RequestMapping(method = RequestMethod.GET, path = "/q7")
    public List<TrainNo> getTrainNoMethod7(@Param("time") String time, @Param("from") String from, @Param("to") String to){
        return trainNoRepository.getTrainNoMethod7(time, from, to);
    }

    // 8. {time}从{location/station}出发，{time}能到{location/station}的<{train_type}>有哪些？
    @RequestMapping(method = RequestMethod.GET, path = "/q8")
    public List<TrainNo> getTrainNoMethod8(@Param("timeStart") String timeStart, @Param("timeEnd") String timeEnd, @Param("from") String from, @Param("to") String to){
        return trainNoRepository.getTrainNoMethod8(timeStart, timeEnd, from, to);
    }



}
