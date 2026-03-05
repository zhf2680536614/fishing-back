package com.fishing.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("biz_post_like")
public class PostLikeEntity {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long postId;

    private Long userId;

    private LocalDateTime createTime;
}
