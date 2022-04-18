package com.project.springbootneo4j.model.relationship;

import com.project.springbootneo4j.model.Province;
import com.project.springbootneo4j.model.Station;
import lombok.Data;
import org.neo4j.ogm.annotation.*;

@Data
@RelationshipEntity(type = "所属省份")
public class SubordinateToProvince {

    @Id
    @GeneratedValue
    private Long id;

    @Property
    private String name;

    @StartNode
    private Station station;

    @EndNode
    private Province province;

}
