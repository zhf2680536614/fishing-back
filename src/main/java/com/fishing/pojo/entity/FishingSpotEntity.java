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
    private String typeDictTypeCode;
    private String typeDictItemCode;
    private Double longitude;
    private Double latitude;
    private String province;
    private String city;
    private String address;
    private String priceDesc;
    private String fishInfoDictTypeCode;
    private String fishInfoDictItemCodes;
    private String images;
    private String bestPositionDesc;
    private Long creatorId;
    private String statusDictTypeCode;
    private String statusDictItemCode;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer isDeleted;
}