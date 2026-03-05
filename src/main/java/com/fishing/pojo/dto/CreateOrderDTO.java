package com.fishing.pojo.dto;

import lombok.Data;

@Data
public class CreateOrderDTO {
    private Long gearId;
    private String statusDictTypeCode;
    private String statusDictItemCode;
    private String address;
    private String contactPhone;
}
