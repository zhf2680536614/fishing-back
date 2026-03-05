package com.fishing.pojo.dto;

import lombok.Data;

@Data
public class BadgeDTO {
    private String badgeName;
    private String badgeIcon;
    private String description;
    private String requirementType;
    private Double requirementValue;
    private Integer sortOrder;
}
