package com.fishing.model;

import lombok.Data;

/**
 * 天气请求模型
 */
@Data
public class WeatherRequest {
    /**
     * 位置信息
     */
    private String location;
    
    /**
     * 温度（摄氏度）
     */
    private double temperature;
    
    /**
     * 天气描述
     */
    private String weather;
    
    /**
     * 风速（级）
     */
    private double windSpeed;
    
    /**
     * 风向
     */
    private String windDirection;
    
    /**
     * 气压（hPa）
     */
    private int pressure;
    
    /**
     * 湿度（%）
     */
    private int humidity;
}
