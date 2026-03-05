package com.fishing.pojo.query;

import lombok.Data;

@Data
public class OrderPageQuery {
    private Long pageNum = 1L;
    private Long pageSize = 10L;
    private String gearTitle;
    private String statusDictItemCode;
    private Long userId;
    private String contactPhone;
    private String startTime;
    private String endTime;
}
