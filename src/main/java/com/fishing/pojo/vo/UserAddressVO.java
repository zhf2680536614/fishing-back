package com.fishing.pojo.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.util.Date;

@Data
public class UserAddressVO {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;
    private String name;
    private String phone;
    private String province;
    private String city;
    private String district;
    private String detailAddress;
    private Integer isDefault;
    private Date createTime;
    private Date updateTime;
    
    // 完整地址（用于显示）
    public String getFullAddress() {
        if (province == null) province = "";
        if (city == null) city = "";
        if (district == null) district = "";
        if (detailAddress == null) detailAddress = "";
        return province + city + district + detailAddress;
    }
}
