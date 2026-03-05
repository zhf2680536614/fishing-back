package com.fishing.pojo.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class FishingSpotManageVO implements Serializable {
    private Long id;
    private String name;
    private String typeDictTypeCode;
    private String typeDictItemCode;
    private String typeDictItemName;
    private Double longitude;
    private Double latitude;
    private String province;
    private String city;
    private String address;
    private String priceDesc;
    private String fishInfoDictTypeCode;
    private List<String> fishInfoDictItemCodes;
    private List<String> fishInfoDictItemNames;
    private List<String> images;
    private String bestPositionDesc;
    private Long creatorId;
    private String creatorName;
    private String statusDictTypeCode;
    private String statusDictItemCode;
    private String statusDictItemName;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
