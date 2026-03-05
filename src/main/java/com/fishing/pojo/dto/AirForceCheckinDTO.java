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
    
    /**
     * 帖子类型字典类型编码
     */
    private String typeDictTypeCode;
    
    /**
     * 帖子类型字典项编码
     */
    private String typeDictItemCode;
    
    /**
     * AI审核状态字典类型编码
     */
    private String aiAuditStatusDictTypeCode;
    
    /**
     * AI审核状态字典项编码
     */
    private String aiAuditStatusDictItemCode;
    
    /**
     * 状态字典类型编码
     */
    private String statusDictTypeCode;
    
    /**
     * 状态字典项编码
     */
    private String statusDictItemCode;
}
