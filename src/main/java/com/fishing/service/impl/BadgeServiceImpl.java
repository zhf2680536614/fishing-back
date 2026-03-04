package com.fishing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fishing.mapper.BadgeDefinitionMapper;
import com.fishing.mapper.UserBadgeMapper;
import com.fishing.pojo.entity.BadgeDefinitionEntity;
import com.fishing.pojo.entity.UserBadgeEntity;
import com.fishing.service.BadgeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BadgeServiceImpl implements BadgeService {

    @Resource
    private BadgeDefinitionMapper badgeDefinitionMapper;

    @Resource
    private UserBadgeMapper userBadgeMapper;

    @Override
    public List<BadgeDefinitionEntity> getAllBadgeDefinitions() {
        QueryWrapper<BadgeDefinitionEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("is_deleted", 0);
        wrapper.orderByAsc("sort_order");
        return badgeDefinitionMapper.selectList(wrapper);
    }

    @Override
    public List<Map<String, Object>> getUserBadges(Long userId) {
        // 获取用户已获得的勋章
        QueryWrapper<UserBadgeEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.eq("is_deleted", 0);
        List<UserBadgeEntity> userBadges = userBadgeMapper.selectList(wrapper);
        
        // 获取所有勋章定义
        List<BadgeDefinitionEntity> allBadges = getAllBadgeDefinitions();
        
        // 将用户勋章与最新的勋章定义合并
        List<Map<String, Object>> result = new ArrayList<>();
        for (UserBadgeEntity userBadge : userBadges) {
            Map<String, Object> badgeData = new HashMap<>();
            badgeData.put("id", userBadge.getId());
            badgeData.put("userId", userBadge.getUserId());
            badgeData.put("obtainDate", userBadge.getObtainDate());
            
            // 查找对应的勋章定义，获取最新的图标和描述
            for (BadgeDefinitionEntity badge : allBadges) {
                if (badge.getId().equals(userBadge.getBadgeId())) {
                    badgeData.put("badgeId", badge.getId());
                    badgeData.put("badgeName", badge.getBadgeName());
                    badgeData.put("badgeIcon", badge.getBadgeIcon());
                    badgeData.put("description", badge.getDescription());
                    break;
                }
            }
            
            result.add(badgeData);
        }
        
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void checkAndAwardBadges(Long userId, int fishingDays, int fishDays, int airForceDays, double totalWeight) {
        // 获取所有勋章定义
        List<BadgeDefinitionEntity> allBadges = getAllBadgeDefinitions();
        // 获取用户已获得的勋章
        List<Map<String, Object>> userBadges = getUserBadges(userId);
        // 提取已获得的勋章ID
        Set<Long> obtainedBadgeIds = userBadges.stream()
                .map(badge -> (Long) badge.get("badgeId"))
                .collect(Collectors.toSet());

        // 检查每个勋章的获取条件
        for (BadgeDefinitionEntity badge : allBadges) {
            if (!obtainedBadgeIds.contains(badge.getId())) {
                boolean shouldAward = false;
                switch (badge.getRequirementType()) {
                    case "fishing_days":
                        shouldAward = fishingDays >= badge.getRequirementValue();
                        break;
                    case "fish_days":
                        shouldAward = fishDays >= badge.getRequirementValue();
                        break;
                    case "air_force_days":
                        shouldAward = airForceDays >= badge.getRequirementValue();
                        break;
                    case "total_weight":
                        shouldAward = totalWeight >= badge.getRequirementValue();
                        break;
                }
                if (shouldAward) {
                    // 颁发勋章
                    UserBadgeEntity userBadge = new UserBadgeEntity();
                    userBadge.setUserId(userId);
                    userBadge.setBadgeId(badge.getId());
                    userBadge.setObtainDate(LocalDateTime.now());
                    userBadge.setIsDeleted(0);
                    userBadgeMapper.insert(userBadge);
                }
            }
        }
    }

    @Override
    public List<BadgeDefinitionEntity> getUnobtainedBadges(Long userId, int fishingDays, int fishDays, int airForceDays, double totalWeight) {
        // 获取所有勋章定义
        List<BadgeDefinitionEntity> allBadges = getAllBadgeDefinitions();
        // 获取用户已获得的勋章
        List<Map<String, Object>> userBadges = getUserBadges(userId);
        // 提取已获得的勋章ID
        Set<Long> obtainedBadgeIds = userBadges.stream()
                .map(badge -> (Long) badge.get("badgeId"))
                .collect(Collectors.toSet());

        // 筛选未获得的勋章
        List<BadgeDefinitionEntity> unobtainedBadges = new ArrayList<>();
        for (BadgeDefinitionEntity badge : allBadges) {
            if (!obtainedBadgeIds.contains(badge.getId())) {
                unobtainedBadges.add(badge);
            }
        }
        return unobtainedBadges;
    }
}