package com.fishing.pojo.dto;

import lombok.Data;

import java.util.List;

/**
 * 空军打卡DTO
 */
@Data
public class AirForceCheckinDTO {
    
    /**
     * 空军经历内容
     */
    private String content;
    
    /**
     * 现场照片URL列表
     */
    private List<String> images;
}
