package com.fishing.pojo.dto;

import lombok.Data;

@Data
public class UserAddressDTO {
    private Long id;
    private String name;
    private String phone;
    private String province;
    private String city;
    private String district;
    private String detailAddress;
    private Integer isDefault;
}