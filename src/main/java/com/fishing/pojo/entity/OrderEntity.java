package com.fishing.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("biz_order")
public class OrderEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long gearId;
    private String gearTitle;
    private BigDecimal gearPrice;
    private BigDecimal totalAmount;
    private String statusDictTypeCode;
    private String statusDictItemCode;
    private String address;
    private String contactPhone;
    private Date createTime;
    private Date updateTime;
    private Integer isDeleted;
}