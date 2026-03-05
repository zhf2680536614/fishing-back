package com.fishing.service;

import com.fishing.pojo.vo.FishingStatsVO;

/**
 * 钓鱼统计服务接口
 */
public interface FishingStatsService {

    /**
     * 获取用户钓鱼统计数据
     * @param userId 用户ID
     * @param catchTypeCode 鱼获战报类型字典项编码
     * @param airForceTypeCode 空军吐槽类型字典项编码
     * @return 钓鱼统计数据
     */
    FishingStatsVO getFishingStats(Long userId, String catchTypeCode, String airForceTypeCode);
}
