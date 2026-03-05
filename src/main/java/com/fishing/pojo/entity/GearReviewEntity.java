package com.fishing.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("biz_gear_review")
public class GearReviewEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String title;
    private String content;
    private BigDecimal rating;
    private String gearName;
    private String categoryDictTypeCode;
    private String categoryDictItemCode;
    private String statusDictTypeCode;
    private String statusDictItemCode;
    private String images;
    private String aiAnalysis;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer isDeleted;
}