package com.fishing.pojo.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class PostCreateDTO {
    private String title;
    private String content;
    private String fishSpecies;
    private BigDecimal fishWeight;
    private String address;
    private List<String> images;
}
