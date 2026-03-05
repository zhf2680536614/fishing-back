package com.fishing.service.impl;

import com.fishing.mapper.CommentMapper;
import com.fishing.mapper.FishingSpotMapper;
import com.fishing.mapper.FishEncyclopediaMapper;
import com.fishing.mapper.OrderMapper;
import com.fishing.mapper.PostMapper;
import com.fishing.mapper.UserMapper;
import com.fishing.pojo.vo.DashboardStatsVO;
import com.fishing.service.DashboardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class DashboardServiceImpl implements DashboardService {
    private final UserMapper userMapper;
    private final PostMapper postMapper;
    private final CommentMapper commentMapper;
    private final OrderMapper orderMapper;
    private final FishingSpotMapper fishingSpotMapper;
    private final FishEncyclopediaMapper fishEncyclopediaMapper;

    public DashboardServiceImpl(UserMapper userMapper, PostMapper postMapper, CommentMapper commentMapper,
                          OrderMapper orderMapper, FishingSpotMapper fishingSpotMapper,
                          FishEncyclopediaMapper fishEncyclopediaMapper) {
        this.userMapper = userMapper;
        this.postMapper = postMapper;
        this.commentMapper = commentMapper;
        this.orderMapper = orderMapper;
        this.fishingSpotMapper = fishingSpotMapper;
        this.fishEncyclopediaMapper = fishEncyclopediaMapper;
    }

    @Override
    public DashboardStatsVO getDashboardStats() {
        DashboardStatsVO stats = new DashboardStatsVO();
        
        LocalDateTime startDate = LocalDateTime.now().minusDays(30);
        
        stats.setTotalUsers(userMapper.countTotalUsers());
        stats.setTodayNewUsers(userMapper.countTodayNewUsers());
        stats.setTotalPosts(postMapper.countTotalPosts());
        stats.setTodayNewPosts(postMapper.countTodayNewPosts());
        stats.setTotalComments(commentMapper.countTotalComments());
        stats.setTotalOrders(orderMapper.countTotalOrders());
        stats.setTotalSpots(fishingSpotMapper.selectCount(null));
        stats.setTotalFish(fishEncyclopediaMapper.selectCount(null));
        stats.setPendingPosts(postMapper.countPendingPosts());
        stats.setPendingReviews(0L);
        stats.setTotalAiCalls(0L);
        
        stats.setUserTrend(userMapper.getUserTrend(startDate));
        stats.setPostTrend(postMapper.getPostTrend(startDate));
        stats.setOrderTrend(orderMapper.getOrderTrend(startDate));
        
        List<DashboardStatsVO.PostTypeDistribution> postTypeDist = postMapper.getPostTypeDistribution();
        long totalPostCount = postTypeDist.stream().mapToLong(DashboardStatsVO.PostTypeDistribution::getCount).sum();
        for (DashboardStatsVO.PostTypeDistribution dist : postTypeDist) {
            if (totalPostCount > 0) {
                dist.setPercentage(new BigDecimal(dist.getCount())
                        .multiply(new BigDecimal("100"))
                        .divide(new BigDecimal(totalPostCount), 2, RoundingMode.HALF_UP));
            } else {
                dist.setPercentage(BigDecimal.ZERO);
            }
        }
        stats.setPostTypeDistribution(postTypeDist);
        
        stats.setFishSpeciesDistribution(List.of());
        
        stats.setLatestUsers(userMapper.getLatestUsers());
        stats.setLatestPosts(postMapper.getLatestPosts());
        stats.setLatestOrders(orderMapper.getLatestOrders());
        
        return stats;
    }
}
