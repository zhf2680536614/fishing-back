package com.fishing.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_badge_definition")
public class BadgeDefinitionEntity {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private String badgeName;
    private String badgeIcon;
    private String description;
    private String requirementType;
    private Double requirementValue;
    private Integer sortOrder;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer isDeleted;
}