package com.fishing.pojo.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BadgeManageVO {
    private Long id;
    private String badgeName;
    private String badgeIcon;
    private String description;
    private String requirementType;
    private Double requirementValue;
    private Integer sortOrder;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
