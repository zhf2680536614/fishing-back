package com.fishing.pojo.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 首页统计数据VO
 */
@Data
public class HomeStatsVO implements Serializable {

    /**
     * 今日出钓人数
     */
    private Long todayFishingCount;

    /**
     * 今日空军人数
     */
    private Long todayAirForceCount;

    /**
     * 今日鱼获总重量(kg)
     */
    private Double todayTotalWeight;
}
