package com.fishing.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("biz_order")
public class OrderEntity {
    private Long id;
    private Long userId;
    private Long gearId;
    private String gearTitle;
    private BigDecimal gearPrice;
    private BigDecimal totalAmount;
    private Integer status;
    private String address;
    private String contactPhone;
    private Date createTime;
    private Date updateTime;
    private Integer isDeleted;
}