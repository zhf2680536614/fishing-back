package com.fishing.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("biz_fishing_spot")
public class FishingSpotEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private Integer type;
    private Double longitude;
    private Double latitude;
    private String province;
    private String city;
    private String address;
    private String priceDesc;
    private String fishInfo;
    private String images;
    private String bestPositionDesc;
    private Long creatorId;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer isDeleted;
}