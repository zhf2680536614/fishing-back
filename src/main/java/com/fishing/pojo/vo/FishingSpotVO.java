package com.fishing.pojo.vo;

import lombok.Data;

import java.util.List;

@Data
public class FishingSpotVO {
    private Long id;
    private String name;
    private Double longitude;
    private Double latitude;
    private String address;
    private String price;
    private String fishInfo;
    private String aiRecommendation;
    private String type;
    private Double rating;
    private Double distance;
    private String image;
    private List<String> images;
}