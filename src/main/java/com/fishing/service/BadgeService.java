package com.fishing.service;

import com.fishing.pojo.entity.BadgeDefinitionEntity;
import com.fishing.pojo.entity.UserBadgeEntity;

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
}