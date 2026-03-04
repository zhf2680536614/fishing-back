package com.fishing.service;

import com.fishing.pojo.vo.FishingStatsVO;

/**
 * 钓鱼统计服务接口
 */
public interface FishingStatsService {

    /**
     * 获取用户钓鱼统计数据
     * @param userId 用户ID
     * @return 钓鱼统计数据
     */
    FishingStatsVO getFishingStats(Long userId);
}
