package com.fishing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fishing.exception.BusinessException;
import com.fishing.mapper.GearReviewMapper;
import com.fishing.mapper.ReviewLikeMapper;
import com.fishing.mapper.UserMapper;
import com.fishing.pojo.dto.GearReviewDTO;
import com.fishing.pojo.entity.GearReviewEntity;
import com.fishing.pojo.entity.ReviewLikeEntity;
import com.fishing.pojo.entity.UserEntity;
import com.fishing.pojo.vo.GearReviewVO;
import com.fishing.service.GearReviewService;
import com.fishing.utils.MinioUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GearReviewServiceImpl implements GearReviewService {
    private final GearReviewMapper gearReviewMapper;
    private final ReviewLikeMapper reviewLikeMapper;
    private final UserMapper userMapper;
    private final MinioUtils minioUtils;
    private final Gson gson = new Gson();

    @Override
    public Page<GearReviewVO> page(int pageNum, int pageSize, String category, String keyword) {
        LambdaQueryWrapper<GearReviewEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(GearReviewEntity::getIsDeleted, 0);
        if (category != null && !category.equals("all")) {
            queryWrapper.eq(GearReviewEntity::getCategory, category);
        }
        if (keyword != null && !keyword.isEmpty()) {
            queryWrapper.like(GearReviewEntity::getTitle, keyword).or()
                        .like(GearReviewEntity::getGearName, keyword);
        }
        queryWrapper.orderByDesc(GearReviewEntity::getCreateTime);

        Page<GearReviewEntity> page = gearReviewMapper.selectPage(new Page<>(pageNum, pageSize), queryWrapper);
        Page<GearReviewVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());

        List<GearReviewVO> records = page.getRecords().stream().map(this::entityToVO).collect(Collectors.toList());
        voPage.setRecords(records);
        return voPage;
    }

    @Override
    public GearReviewVO getById(Long id) {
        GearReviewEntity entity = gearReviewMapper.selectById(id);
        if (entity == null || entity.getIsDeleted() == 1) {
            throw new BusinessException("测评不存在");
        }
        // 增加浏览量
        entity.setViews(entity.getViews() + 1);
        gearReviewMapper.updateById(entity);
        return entityToVO(entity);
    }

    @Override
    public void save(GearReviewDTO dto, Long userId) {
        GearReviewEntity entity = new GearReviewEntity();
        entity.setUserId(userId);
        entity.setTitle(dto.getTitle());
        entity.setContent(dto.getContent());
        entity.setRating(dto.getRating());
        entity.setGearName(dto.getGearName());
        entity.setCategory(dto.getCategory());
        entity.setViews(0);
        entity.setLikes(0);
        entity.setComments(0);
        entity.setAiAnalysis(generateAiAnalysis(dto.getContent()));
        entity.setIsDeleted(0);
        gearReviewMapper.insert(entity);
    }

    @Override
    public void update(Long id, GearReviewDTO dto, Long userId) {
        GearReviewEntity entity = gearReviewMapper.selectById(id);
        if (entity == null || entity.getIsDeleted() == 1) {
            throw new BusinessException("测评不存在");
        }
        if (!entity.getUserId().equals(userId)) {
            throw new BusinessException("无权修改");
        }
        entity.setTitle(dto.getTitle());
        entity.setContent(dto.getContent());
        entity.setRating(dto.getRating());
        entity.setGearName(dto.getGearName());
        entity.setCategory(dto.getCategory());
        entity.setAiAnalysis(generateAiAnalysis(dto.getContent()));
        gearReviewMapper.updateById(entity);
    }

    @Override
    public void delete(Long id, Long userId) {
        GearReviewEntity entity = gearReviewMapper.selectById(id);
        if (entity == null || entity.getIsDeleted() == 1) {
            throw new BusinessException("测评不存在");
        }
        if (!entity.getUserId().equals(userId)) {
            throw new BusinessException("无权删除");
        }
        entity.setIsDeleted(1);
        gearReviewMapper.updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void like(Long reviewId, Long userId) {
        // 检查是否已点赞
        LambdaQueryWrapper<ReviewLikeEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ReviewLikeEntity::getReviewId, reviewId);
        queryWrapper.eq(ReviewLikeEntity::getUserId, userId);
        if (reviewLikeMapper.selectOne(queryWrapper) != null) {
            throw new BusinessException("已经点赞过");
        }

        // 增加点赞记录
        ReviewLikeEntity likeEntity = new ReviewLikeEntity();
        likeEntity.setReviewId(reviewId);
        likeEntity.setUserId(userId);
        reviewLikeMapper.insert(likeEntity);

        // 更新点赞数
        GearReviewEntity reviewEntity = gearReviewMapper.selectById(reviewId);
        if (reviewEntity != null) {
            reviewEntity.setLikes(reviewEntity.getLikes() + 1);
            gearReviewMapper.updateById(reviewEntity);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unlike(Long reviewId, Long userId) {
        // 删除点赞记录
        LambdaQueryWrapper<ReviewLikeEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ReviewLikeEntity::getReviewId, reviewId);
        queryWrapper.eq(ReviewLikeEntity::getUserId, userId);
        int result = reviewLikeMapper.delete(queryWrapper);
        if (result == 0) {
            throw new BusinessException("未点赞");
        }

        // 更新点赞数
        GearReviewEntity reviewEntity = gearReviewMapper.selectById(reviewId);
        if (reviewEntity != null && reviewEntity.getLikes() > 0) {
            reviewEntity.setLikes(reviewEntity.getLikes() - 1);
            gearReviewMapper.updateById(reviewEntity);
        }
    }

    @Override
    public boolean isLiked(Long reviewId, Long userId) {
        LambdaQueryWrapper<ReviewLikeEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ReviewLikeEntity::getReviewId, reviewId);
        queryWrapper.eq(ReviewLikeEntity::getUserId, userId);
        return reviewLikeMapper.selectOne(queryWrapper) != null;
    }

    private GearReviewVO entityToVO(GearReviewEntity entity) {
        GearReviewVO vo = new GearReviewVO();
        vo.setId(entity.getId());
        vo.setUserId(entity.getUserId());
        
        UserEntity user = userMapper.selectById(entity.getUserId());
        if (user != null) {
            vo.setUsername(user.getUsername());
            vo.setNickname(user.getNickname());
            vo.setAvatar(minioUtils.getFullUrl(user.getAvatar(), "user_avatar"));
        }
        
        vo.setTitle(entity.getTitle());
        vo.setContent(entity.getContent());
        vo.setRating(entity.getRating());
        vo.setGearName(entity.getGearName());
        vo.setCategory(entity.getCategory());
        vo.setViews(entity.getViews());
        vo.setLikes(entity.getLikes());
        vo.setComments(entity.getComments());
        if (entity.getAiAnalysis() != null) {
            vo.setAiAnalysis(gson.fromJson(entity.getAiAnalysis(), new TypeToken<Map<String, Object>>(){}.getType()));
        }
        vo.setCreateTime(entity.getCreateTime());
        return vo;
    }

    private String generateAiAnalysis(String content) {
        // 简单的AI分析模拟
        Map<String, Object> analysis = new HashMap<>();
        int goodCount = 0;
        int badCount = 0;
        
        String[] goodWords = {"好", "棒", "优秀", "满意", "推荐"};
        String[] badWords = {"差", "不好", "失望", "不推荐"};
        
        for (String word : goodWords) {
            if (content.contains(word)) goodCount++;
        }
        for (String word : badWords) {
            if (content.contains(word)) badCount++;
        }
        
        int total = goodCount + badCount;
        int goodRate = total > 0 ? (goodCount * 100) / total : 50;
        analysis.put("goodRate", goodRate);
        
        // 提取关键词
        List<String> keywords = new ArrayList<>();
        if (content.contains("鱼竿")) keywords.add("鱼竿");
        if (content.contains("钓箱")) keywords.add("钓箱");
        if (content.contains("饵料")) keywords.add("饵料");
        if (content.contains("质量")) keywords.add("质量");
        if (content.contains("价格")) keywords.add("价格");
        if (content.contains("手感")) keywords.add("手感");
        if (content.contains("耐用")) keywords.add("耐用");
        if (keywords.isEmpty()) keywords.add("装备");
        analysis.put("keywords", keywords);
        
        return gson.toJson(analysis);
    }
}