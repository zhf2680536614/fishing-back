package com.fishing.pojo.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class GearMarketDTO {
    private Long userId;
    private String title;
    private String description;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private List<String> images;
    private String categoryDictTypeCode;
    private String categoryDictItemCode;
    private String statusDictTypeCode;
    private String statusDictItemCode;
}