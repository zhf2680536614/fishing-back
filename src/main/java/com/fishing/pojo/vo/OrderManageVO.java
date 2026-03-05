package com.fishing.pojo.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderManageVO {
    private Long id;
    private Long userId;
    private String username;
    private String nickname;
    private String avatar;
    private String phone;
    private Long gearId;
    private String gearTitle;
    private BigDecimal gearPrice;
    private BigDecimal totalAmount;
    private String statusDictTypeCode;
    private String statusDictItemCode;
    private String statusDictItemName;
    private String address;
    private String contactPhone;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
