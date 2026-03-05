package com.fishing.pojo.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class OrderVO {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    private Long userId;
    private Long gearId;
    private String gearTitle;
    private String gearImages;
    private BigDecimal gearPrice;
    private BigDecimal totalAmount;
    private String statusDictItemCode;
    private String statusText;
    private String address;
    private String contactPhone;
    private Date createTime;
    private Date updateTime;
}
