package com.fishing.service;

import com.fishing.pojo.PageResult;
import com.fishing.pojo.dto.FishingSpotCreateDTO;
import com.fishing.pojo.dto.FishingSpotUpdateDTO;
import com.fishing.pojo.query.FishingSpotPageQuery;
import com.fishing.pojo.vo.FishingSpotManageVO;
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

    /**
     * 分页查询钓点列表（管理后台）
     */
    PageResult<FishingSpotManageVO> page(FishingSpotPageQuery query);

    /**
     * 根据ID获取钓点管理详情
     */
    FishingSpotManageVO getSpotManageById(Long id);

    /**
     * 创建钓点
     */
    void createSpot(FishingSpotCreateDTO dto);

    /**
     * 更新钓点
     */
    void updateSpot(Long id, FishingSpotUpdateDTO dto);

    /**
     * 删除钓点
     */
    void deleteSpot(Long id);
}