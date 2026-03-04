package com.fishing.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("biz_review_comment")
public class ReviewCommentEntity {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long reviewId;
    private Long userId;
    private String content;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer isDeleted;
}