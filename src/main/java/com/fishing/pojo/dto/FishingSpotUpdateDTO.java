package com.fishing.pojo.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class FishingSpotUpdateDTO implements Serializable {
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
    private List<String> fishInfoDictItemCodes;
    private List<String> images;
    private String bestPositionDesc;
    private String statusDictTypeCode;
    private String statusDictItemCode;
}
