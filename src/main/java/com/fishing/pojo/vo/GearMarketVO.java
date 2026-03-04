package com.fishing.pojo.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class GearMarketVO {
    private Long id;
    private Long userId;
    private String username;
    private String nickname;
    private String avatar;
    private String title;
    private String description;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private List<String> images;
    private String category;
    private Integer status;
    private LocalDateTime createTime;
}