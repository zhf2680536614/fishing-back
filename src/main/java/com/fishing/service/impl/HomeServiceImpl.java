package com.fishing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fishing.mapper.PostMapper;
import com.fishing.mapper.UserMapper;
import com.fishing.pojo.entity.PostEntity;
import com.fishing.pojo.entity.UserEntity;
import com.fishing.pojo.vo.HomeStatsVO;
import com.fishing.pojo.vo.HotPostVO;
import com.fishing.service.HomeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 首页服务实现
 */
@Service
@Slf4j
public class HomeServiceImpl implements HomeService {

    private final PostMapper postMapper;
    private final UserMapper userMapper;

    public HomeServiceImpl(PostMapper postMapper, UserMapper userMapper) {
        this.postMapper = postMapper;
        this.userMapper = userMapper;
    }

    @Override
    public HomeStatsVO getHomeStats() {
        HomeStatsVO stats = new HomeStatsVO();

        // 获取今天的开始和结束时间
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime todayEnd = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);

        // 查询今日战报帖子（type=0 表示战报帖子）
        LambdaQueryWrapper<PostEntity> fishingQuery = new LambdaQueryWrapper<PostEntity>()
                .eq(PostEntity::getTypeDictItemCode, "catch_report")
                .eq(PostEntity::getIsDeleted, 0)
                .between(PostEntity::getCreateTime, todayStart, todayEnd);

        List<PostEntity> todayPosts = postMapper.selectList(fishingQuery);

        // 今日出钓人数（去重用户数）
        long todayFishingCount = todayPosts.stream()
                .map(PostEntity::getUserId)
                .distinct()
                .count();
        stats.setTodayFishingCount(todayFishingCount);

        // 今日鱼获总重量
        double todayTotalWeight = todayPosts.stream()
                .map(PostEntity::getFishWeight)
                .filter(weight -> weight != null)
                .mapToDouble(BigDecimal::doubleValue)
                .sum();
        stats.setTodayTotalWeight(todayTotalWeight);

        // 查询今日空军帖子（type=1 表示空军帖子）
        LambdaQueryWrapper<PostEntity> airForceQuery = new LambdaQueryWrapper<PostEntity>()
                .eq(PostEntity::getTypeDictItemCode, "air_force")
                .eq(PostEntity::getIsDeleted, 0)
                .between(PostEntity::getCreateTime, todayStart, todayEnd);

        long todayAirForceCount = postMapper.selectCount(airForceQuery);
        stats.setTodayAirForceCount(todayAirForceCount);

        log.info("获取首页统计数据：今日出钓={}, 今日空军={}, 今日鱼获={}kg",
                todayFishingCount, todayAirForceCount, todayTotalWeight);

        return stats;
    }

    @Override
    public List<HotPostVO> getTodayHotPosts() {
        // 获取今天的开始和结束时间
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime todayEnd = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);

        // 查询今日战报帖子，按鱼重量降序排列，取前3名
        LambdaQueryWrapper<PostEntity> query = new LambdaQueryWrapper<PostEntity>()
                .eq(PostEntity::getTypeDictItemCode, "catch_report")
                .eq(PostEntity::getIsDeleted, 0)
                .between(PostEntity::getCreateTime, todayStart, todayEnd)
                .orderByDesc(PostEntity::getFishWeight)
                .last("LIMIT 3");

        List<PostEntity> posts = postMapper.selectList(query);

        return posts.stream().map(this::convertToHotPostVO).collect(Collectors.toList());
    }

    private HotPostVO convertToHotPostVO(PostEntity entity) {
        HotPostVO vo = new HotPostVO();
        vo.setId(entity.getId());
        vo.setTitle(entity.getTitle());
        vo.setFishSpecies(entity.getFishSpeciesDictItemCode());
        vo.setFishWeight(entity.getFishWeight());
        vo.setCreateTime(entity.getCreateTime());

        // 获取第一张图片作为封面
        if (entity.getImages() != null && !entity.getImages().isEmpty()) {
            String imagesStr = entity.getImages();
            if (imagesStr.startsWith("[")) {
                // JSON数组格式
                try {
                    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                    List<String> images = mapper.readValue(imagesStr, mapper.getTypeFactory().constructCollectionType(List.class, String.class));
                    vo.setCoverImage(images.isEmpty() ? null : images.get(0));
                } catch (Exception e) {
                    log.error("解析图片JSON失败", e);
                    vo.setCoverImage(null);
                }
            } else {
                // 单张图片或逗号分隔
                vo.setCoverImage(imagesStr.split(",")[0]);
            }
        }

        // 获取用户信息
        UserEntity user = userMapper.selectById(entity.getUserId());
        if (user != null) {
            vo.setUserNickname(user.getNickname());
            vo.setUserAvatar(user.getAvatar());
        }

        return vo;
    }
}
