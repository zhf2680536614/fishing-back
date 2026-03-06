package com.fishing.pojo.dto;

import lombok.Data;

@Data
public class FishEncyclopediaDTO {
    private String name;
    private String alias;
    private String category;
    private Integer protectionLevel;
    private String habits;
    private String edibleValue;
    private String images;
}
