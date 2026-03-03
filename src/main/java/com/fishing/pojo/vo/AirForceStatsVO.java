package com.fishing.pojo.vo;

import lombok.Data;

/**
 * 空军统计数据VO
 */
@Data
public class AirForceStatsVO {
    
    /**
     * 今日空军人数
     */
    private Integer todayAirForce;
    
    /**
     * 本周空军人数
     */
    private Integer weekAirForce;
    
    /**
     * 空军率（百分比）
     */
    private Integer airForceRate;
    
    /**
     * 最长连续空军天数
     */
    private Integer maxStreak;
    
    /**
     * 我的空军次数
     */
    private Integer myAirForceCount;
    
    /**
     * 我的连续空军天数
     */
    private Integer myStreak;
}
