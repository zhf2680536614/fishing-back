package com.fishing.pojo.vo;

import lombok.Data;

@Data
public class FishVO {

    private Long id;

    private String name;

    private String alias;

    private String category;

    private Boolean isProtected;

    private String habits;

    private String edibleValue;

    private String images;
}
