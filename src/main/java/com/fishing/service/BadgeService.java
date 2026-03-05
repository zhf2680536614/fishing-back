package com.fishing.service;

import com.fishing.pojo.dto.BadgeDTO;
import com.fishing.pojo.entity.BadgeDefinitionEntity;
import com.fishing.pojo.entity.UserBadgeEntity;
import com.fishing.pojo.PageResult;
import com.fishing.pojo.query.BadgePageQuery;
import com.fishing.pojo.vo.BadgeManageVO;

import java.util.List;
import java.util.Map;

public interface BadgeService {
    /**
     * 获取所有勋章定义
     */
    List<BadgeDefinitionEntity> getAllBadgeDefinitions();

    /**
     * 获取用户已获得的勋章
     */
    List<Map<String, Object>> getUserBadges(Long userId);

    /**
     * 检查并颁发勋章
     */
    void checkAndAwardBadges(Long userId, int fishingDays, int fishDays, int airForceDays, double totalWeight);

    /**
     * 获取用户未获得的勋章
     */
    List<BadgeDefinitionEntity> getUnobtainedBadges(Long userId, int fishingDays, int fishDays, int airForceDays, double totalWeight);

    /**
     * 分页查询勋章列表（管理后台）
     */
    PageResult<BadgeManageVO> page(BadgePageQuery query);

    /**
     * 根据ID获取勋章详情（管理后台）
     */
    BadgeManageVO getBadgeManageById(Long id);

    /**
     * 创建勋章（管理后台）
     */
    void createBadge(BadgeDTO dto);

    /**
     * 更新勋章（管理后台）
     */
    void updateBadge(Long id, BadgeDTO dto);

    /**
     * 删除勋章（管理后台）
     */
    void deleteBadge(Long id);
}