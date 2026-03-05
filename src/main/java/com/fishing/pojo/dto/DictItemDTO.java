package com.fishing.pojo.dto;

import lombok.Data;

@Data
public class DictItemDTO {
    private Long dictTypeId;
    private String itemCode;
    private String itemName;
    private String value;
    private String description;
    private Integer sortOrder;
    private Integer status;
}
