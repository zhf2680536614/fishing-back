package com.fishing.pojo.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class DashboardStatsVO {
    private Long totalUsers;
    private Long todayNewUsers;
    private Long totalPosts;
    private Long todayNewPosts;
    private Long totalComments;
    private Long totalOrders;
    private Long totalSpots;
    private Long totalFish;
    private Long pendingPosts;
    private Long pendingReviews;
    private Long totalAiCalls;
    private List<TrendData> userTrend;
    private List<TrendData> postTrend;
    private List<TrendData> orderTrend;
    private List<PostTypeDistribution> postTypeDistribution;
    private List<FishSpeciesDistribution> fishSpeciesDistribution;
    private List<LatestUser> latestUsers;
    private List<LatestPost> latestPosts;
    private List<LatestOrder> latestOrders;

    @Data
    public static class TrendData {
        private String date;
        private Long value;
    }

    @Data
    public static class PostTypeDistribution {
        private String typeName;
        private Long count;
        private BigDecimal percentage;
    }

    @Data
    public static class FishSpeciesDistribution {
        private String fishName;
        private Long count;
    }

    @Data
    public static class LatestUser {
        private Long id;
        private String username;
        private String nickname;
        private String avatar;
        private String createTime;
    }

    @Data
    public static class LatestPost {
        private Long id;
        private String title;
        private String username;
        private String avatar;
        private String createTime;
    }

    @Data
    public static class LatestOrder {
        private Long id;
        private String gearTitle;
        private BigDecimal totalAmount;
        private String status;
        private String createTime;
    }
}
