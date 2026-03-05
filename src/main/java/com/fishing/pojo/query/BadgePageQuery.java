package com.fishing.pojo.query;

import lombok.Data;

@Data
public class BadgePageQuery {
    private Long pageNum = 1L;
    private Long pageSize = 10L;
    private String badgeName;
    private String requirementType;
}
