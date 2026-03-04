package com.fishing.pojo.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class OrderVO {
    private Long id;
    private Long userId;
    private Long gearId;
    private String gearTitle;
    private BigDecimal gearPrice;
    private BigDecimal totalAmount;
    private Integer status;
    private String statusText;
    private String address;
    private String contactPhone;
    private Date createTime;
    private Date updateTime;
}
