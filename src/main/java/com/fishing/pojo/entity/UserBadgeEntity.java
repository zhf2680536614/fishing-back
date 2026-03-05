package com.fishing.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_user_badge")
public class UserBadgeEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long badgeId;
    private LocalDateTime obtainDate;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer isDeleted;
}
