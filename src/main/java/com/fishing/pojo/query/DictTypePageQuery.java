package com.fishing.pojo.query;

import lombok.Data;

@Data
public class DictTypePageQuery {
    private Long pageNum = 1L;
    private Long pageSize = 10L;
    private String dictCode;
    private String dictName;
    private Integer status;
}
