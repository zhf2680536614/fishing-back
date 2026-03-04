package com.fishing.pojo.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class GearReviewDTO {
    private String title;
    private String content;
    private BigDecimal rating;
    private String gearName;
    private String category;
}