package com.my.inventory.dto.rep;

import lombok.Data;

@Data
public class ItemDefinitionRep {

    private Long id;
    private String name;
    private String code;
    private Integer type;
    private String unit;
    private String description;
    private String creatorName;
    private String operatorName;

}
