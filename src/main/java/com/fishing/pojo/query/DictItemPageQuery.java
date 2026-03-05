package com.fishing.pojo.query;

import lombok.Data;

@Data
public class DictItemPageQuery {
    private Long pageNum = 1L;
    private Long pageSize = 10L;
    private Long dictTypeId;
    private String itemCode;
    private String itemName;
    private Integer status;
}
