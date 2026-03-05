package com.fishing.pojo.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class GearReviewDTO {
    private String title;
    private String content;
    private BigDecimal rating;
    private String gearName;
    private String categoryDictTypeCode;
    private String categoryDictItemCode;
    private String statusDictTypeCode;
    private String statusDictItemCode;
    private List<String> images;
}