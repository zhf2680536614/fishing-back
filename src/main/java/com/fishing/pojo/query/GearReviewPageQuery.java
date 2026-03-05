package com.fishing.pojo.query;

import lombok.Data;

import java.io.Serializable;

@Data
public class GearReviewPageQuery implements Serializable {
    private Long pageNum = 1L;
    private Long pageSize = 10L;
    private String title;
    private String gearName;
    private String categoryDictItemCode;
    private String statusDictItemCode;
    private Long userId;
}
