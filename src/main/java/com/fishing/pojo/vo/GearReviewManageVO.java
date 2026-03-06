package com.fishing.pojo.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
public class GearReviewManageVO implements Serializable {
    private Long id;
    private Long userId;
    private String username;
    private String nickname;
    private String avatar;
    private String userNickname;
    private String userAvatar;
    private String title;
    private String content;
    private BigDecimal rating;
    private String gearName;
    private String categoryDictTypeCode;
    private String categoryDictItemCode;
    private String categoryDictItemName;
    private String statusDictTypeCode;
    private String statusDictItemCode;
    private String statusDictItemName;
    private List<String> images;
    private Map<String, Object> aiAnalysis;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
