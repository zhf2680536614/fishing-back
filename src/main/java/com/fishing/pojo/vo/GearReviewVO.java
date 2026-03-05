package com.fishing.pojo.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Data
public class GearReviewVO {
    private Long id;
    private Long userId;
    private String username;
    private String nickname;
    private String avatar;
    private String title;
    private String content;
    private BigDecimal rating;
    private String gearName;
    private String categoryDictItemCode;
    private String statusDictItemCode;
    private java.util.List<String> images;
    private Map<String, Object> aiAnalysis;
    private LocalDateTime createTime;
}