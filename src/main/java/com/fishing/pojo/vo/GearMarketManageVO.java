package com.fishing.pojo.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class GearMarketManageVO implements Serializable {
    private Long id;
    private Long userId;
    private String username;
    private String nickname;
    private String avatar;
    private String phone;
    private String title;
    private String description;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private List<String> images;
    private String categoryDictTypeCode;
    private String categoryDictItemCode;
    private String categoryDictItemName;
    private String statusDictTypeCode;
    private String statusDictItemCode;
    private String statusDictItemName;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
