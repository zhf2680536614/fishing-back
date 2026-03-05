package com.fishing.pojo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PageResult<T> implements Serializable {
    private List<T> list;
    private Long total;
    private Long pageNum;
    private Long pageSize;
}
