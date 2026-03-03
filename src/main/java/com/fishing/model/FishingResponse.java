package com.fishing.model;

import lombok.Data;

/**
 * 垂钓指数响应模型
 */
@Data
public class FishingResponse {
    /**
     * 适钓指数（0-100）
     */
    private int fishingScore;
    
    /**
     * 适钓状态
     */
    private String fishingStatus;
    
    /**
     * AI 垂钓建议
     */
    private String aiAdvice;
    
    /**
     * 气压状态
     */
    private String pressureStatus;
    
    /**
     * 温度状态
     */
    private String temperatureStatus;
    
    /**
     * 风力状态
     */
    private String windStatus;
}
