package com.fishing.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("biz_review_like")
public class ReviewLikeEntity {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long reviewId;
    private Long userId;
    private LocalDateTime createTime;
}