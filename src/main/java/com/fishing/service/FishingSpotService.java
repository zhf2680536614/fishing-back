package com.fishing.service;

import com.fishing.pojo.vo.FishingSpotVO;
import java.util.List;

public interface FishingSpotService {
    /**
     * 获取钓点列表
     */
    List<FishingSpotVO> getSpotList();

    /**
     * 获取推荐钓点（随机指定数量，支持类型筛选）
     */
    List<FishingSpotVO> getRecommendSpots(int limit, String typeDictItemCode);

    /**
     * 搜索钓点（支持关键词和类型筛选）
     */
    List<FishingSpotVO> searchSpots(String keyword, String typeDictItemCode);

    /**
     * 根据ID获取钓点详情
     */
    FishingSpotVO getSpotById(Long id);

    /**
     * 获取AI推荐的最佳钓位
     */
    String getAiRecommendation(Long spotId);
}