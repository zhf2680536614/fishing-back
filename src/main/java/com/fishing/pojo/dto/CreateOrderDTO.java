package com.fishing.pojo.dto;

import lombok.Data;

@Data
public class CreateOrderDTO {
    private Long gearId;
    private String address;
    private String contactPhone;
}
