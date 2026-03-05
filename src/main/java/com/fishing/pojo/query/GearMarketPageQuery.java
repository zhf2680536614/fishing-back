package com.fishing.pojo.query;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class GearMarketPageQuery implements Serializable {
    private Long pageNum = 1L;
    private Long pageSize = 10L;
    private String title;
    private String categoryDictItemCode;
    private String statusDictItemCode;
    private Long userId;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
}
