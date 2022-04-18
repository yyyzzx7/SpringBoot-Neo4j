package com.project.springbootneo4j.repository;

import com.project.springbootneo4j.model.TrainNo;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrainNoRepository extends Neo4jRepository<TrainNo, Long> {

    // 2. 途径{location/station}的<{train_type}>有哪些？
    @Query("MATCH (no:TrainNo)-[:`站点信息`]->(:TrainNode)-[:`途径`]->(s:Station) WHERE s.name contains {name} RETURN no")
    List<TrainNo> getTrainNoMethod2(@Param("name") String name);


    // 3. 从{location/station}到{location/station}有什么<{train_type}>？
    @Query("MATCH (s1:Station)<-[:`途径`]-(id1:TrainNode)<-[:`站点信息`]-(no:TrainNo)-[:`站点信息`]->(id2:TrainNode)-[:`途径`]->(s2:Station)\n" +
            "WHERE s1.name contains {from} AND s2.name contains {to}\n" +
            "WITH id1,no,id2\n" +
            "MATCH (id1)-[:`站点顺序`]->(seq1:TrainInfo),(id2)-[:`站点顺序`]->(seq2:TrainInfo)\n" +
            "WHERE toInt(seq1.name) < toInt(seq2.name)\n" +
            "RETURN no")
    List<TrainNo> getTrainNoMethod3(@Param("from") String from, @Param("to") String to);


    // 4. 终点站是{location/station}的<{train_type}>有哪些？
    @Query("MATCH (s:Station)<-[:`途径`]-(id:TrainNode)-[:`站点性质`]->(:TrainInfo{name:'1'}) WHERE s.name contains {name}\n" +
            "WITH id\n" +
            "MATCH (id)<-[:`站点信息`]-(no:TrainNo)\n" +
            "RETURN no")
    List<TrainNo> getTrainNoMethod4(@Param("name") String name);


    // 5. 从{location/station}出发的<{train_type}>有哪些？
    @Query("MATCH (s:Station)<-[:`途径`]-(id:TrainNode)-[:`站点性质`]->(:TrainInfo{name:'0'}) WHERE s.name contains {name} and id.name ends with \"-1\"\n" +
            "WITH id\n" +
            "MATCH (id)<-[:`站点信息`]-(no:TrainNo)\n" +
            "RETURN no")
    List<TrainNo> getTrainNoMethod5(@Param("name") String name);


    // 6. {time}从{location/station}到{location/station}有什么<{train_type}>？
    @Query("MATCH (s1:Station)<-[:`途径`]-(id1:TrainNode)<-[:`站点信息`]-(no:TrainNo)-[:`站点信息`]->(id2:TrainNode)-[:`途径`]->(s2:Station) WHERE s1.name contains {from} AND s2.name contains {to}\n" +
            "WITH id1,no,id2\n" +
            "MATCH (id1)-[:`站点顺序`]->(seq1:TrainInfo),(id2)-[:`站点顺序`]->(seq2:TrainInfo),(id1)-[:`发车时间`]->(t:TrainInfo)\n" +
            "WHERE toInt(seq1.name) < toInt(seq2.name) AND localtime({time})<=localtime(t.name)<=localtime({time})+duration({minutes:30})\n" +
            "RETURN no")
    List<TrainNo> getTrainNoMethod6(@Param("time") String time, @Param("from") String from, @Param("to") String to);


    // 7. 从{location/station}出发，{time}能到{location/station}的<{train_type}>有哪些？
    @Query("MATCH (s1:Station)<-[:`途径`]-(id1:TrainNode)<-[:`站点信息`]-(no:TrainNo)-[:`站点信息`]->(id2:TrainNode)-[:`途径`]->(s2:Station) WHERE s1.name contains {from} AND s2.name contains {to}\n" +
            "WITH id1,no,id2\n" +
            "MATCH (id1)-[:`站点顺序`]->(seq1:TrainInfo),(id2)-[:`站点顺序`]->(seq2:TrainInfo),(id2)-[r:`发车时间`]->(t:TrainInfo)\n" +
            "WHERE toInt(seq1.name) < toInt(seq2.name) AND localtime(t.name)<=localtime({time})\n" +
            "RETURN no")
    List<TrainNo> getTrainNoMethod7(@Param("time") String time, @Param("from") String from, @Param("to") String to);


    // 8. {time}从{location/station}出发，{time}能到{location/station}的<{train_type}>有哪些？
    @Query("MATCH (s1:Station)<-[:`途径`]-(id1:TrainNode)<-[:`站点信息`]-(no:TrainNo)-[:`站点信息`]->(id2:TrainNode)-[:`途径`]->(s2:Station) WHERE s1.name contains {from} AND s2.name contains {to}\n" +
            "WITH id1,no,id2\n" +
            "MATCH (id1)-[:`发车时间`]->(t1:TrainInfo),(id2)-[:`到达时间`]->(t2:TrainInfo),(id1)-[:`站点顺序`]->(seq1:TrainInfo),(id2)-[:`站点顺序`]->(seq2:TrainInfo)\n" +
            "WHERE localtime({timeStart})<=localtime(t1.name)<= localtime({timeStart})+duration({minutes:30})\n" +
            "AND localtime(t2.name)<=localtime({timeEnd})\n" +
            "AND localtime(t1.name)<localtime(t2.name)\n" +
            "AND toInt(seq1.name) < toInt(seq2.name)\n" +
            "RETURN no")
    List<TrainNo> getTrainNoMethod8(@Param("timeStart") String timeStart, @Param("timeEnd") String timeEnd, @Param("from") String from, @Param("to") String to);




}
