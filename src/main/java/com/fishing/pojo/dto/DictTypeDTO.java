package com.fishing.pojo.dto;

import lombok.Data;

@Data
public class DictTypeDTO {
    private String dictCode;
    private String dictName;
    private Long parentId;
    private String description;
    private Integer sortOrder;
    private Integer status;
}
