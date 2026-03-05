package com.fishing.pojo.query;

import lombok.Data;

import java.io.Serializable;

@Data
public class FishingSpotPageQuery implements Serializable {
    private Long pageNum = 1L;
    private Long pageSize = 10L;
    private String name;
    private String province;
    private String city;
    private String typeDictItemCode;
    private String statusDictItemCode;
}
