package com.fishing.pojo.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 钓鱼统计数据VO
 */
@Data
public class FishingStatsVO implements Serializable {

    /**
     * 每月出钓率数据
     */
    private List<MonthlyFishingRateVO> monthlyFishingRates;

    /**
     * 每日鱼获重量趋势（最近15天）
     */
    private List<DailyFishWeightVO> dailyFishWeights;

    /**
     * 每月空军率数据
     */
    private List<MonthlyAirForceRateVO> monthlyAirForceRates;

    /**
     * 每月出钓率详情
     */
    @Data
    public static class MonthlyFishingRateVO implements Serializable {
        /**
         * 月份（格式：yyyy-MM）
         */
        private String month;

        /**
         * 月份显示名称（格式：yyyy年MM月）
         */
        private String monthName;

        /**
         * 出钓天数
         */
        private Integer fishingDays;

        /**
         * 当月总天数
         */
        private Integer totalDays;

        /**
         * 出钓率（百分比）
         */
        private Double rate;
    }

    /**
     * 每日鱼获重量详情
     */
    @Data
    public static class DailyFishWeightVO implements Serializable {
        /**
         * 日期（格式：MM-dd）
         */
        private String date;

        /**
         * 完整日期（格式：yyyy-MM-dd）
         */
        private String fullDate;

        /**
         * 鱼获总重量（斤）
         */
        private Double totalWeight;

        /**
         * 是否有钓鱼记录
         */
        private Boolean hasRecord;
    }

    /**
     * 每月空军率详情
     */
    @Data
    public static class MonthlyAirForceRateVO implements Serializable {
        /**
         * 月份（格式：yyyy-MM）
         */
        private String month;

        /**
         * 月份显示名称（格式：yyyy年MM月）
         */
        private String monthName;

        /**
         * 出钓次数
         */
        private Integer totalTrips;

        /**
         * 空军次数
         */
        private Integer airForceCount;

        /**
         * 空军率（百分比）
         */
        private Double rate;
    }
}
