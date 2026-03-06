package com.fishing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fishing.mapper.FishEncyclopediaMapper;
import com.fishing.pojo.PageResult;
import com.fishing.pojo.dto.FishEncyclopediaDTO;
import com.fishing.pojo.entity.FishEncyclopediaEntity;
import com.fishing.pojo.query.FishEncyclopediaPageQuery;
import com.fishing.pojo.vo.FishManageVO;
import com.fishing.service.FishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FishServiceImpl implements FishService {
    private final FishEncyclopediaMapper fishEncyclopediaMapper;
    private final ObjectMapper objectMapper;

    public FishServiceImpl(FishEncyclopediaMapper fishEncyclopediaMapper) {
        this.fishEncyclopediaMapper = fishEncyclopediaMapper;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public PageResult<FishManageVO> pageFish(FishEncyclopediaPageQuery query) {
        Page<FishEncyclopediaEntity> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<FishEncyclopediaEntity> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(query.getName())) {
            wrapper.like(FishEncyclopediaEntity::getName, query.getName());
        }
        if (StringUtils.hasText(query.getAlias())) {
            wrapper.like(FishEncyclopediaEntity::getAlias, query.getAlias());
        }
        if (StringUtils.hasText(query.getCategory())) {
            wrapper.like(FishEncyclopediaEntity::getCategory, query.getCategory());
        }
        if (query.getProtectionLevel() != null) {
            wrapper.eq(FishEncyclopediaEntity::getProtectionLevel, query.getProtectionLevel());
        }

        wrapper.eq(FishEncyclopediaEntity::getIsDeleted, 0)
                .orderByDesc(FishEncyclopediaEntity::getCreateTime);

        IPage<FishEncyclopediaEntity> entityPage = fishEncyclopediaMapper.selectPage(page, wrapper);
        PageResult<FishManageVO> result = new PageResult<>();
        result.setList(entityPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList()));
        result.setTotal(entityPage.getTotal());
        result.setPageNum(entityPage.getCurrent());
        result.setPageSize(entityPage.getSize());
        return result;
    }

    @Override
    public FishManageVO getFishById(Long id) {
        FishEncyclopediaEntity entity = fishEncyclopediaMapper.selectById(id);
        if (entity == null || entity.getIsDeleted() == 1) {
            return null;
        }
        return convertToVO(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createFish(FishEncyclopediaDTO dto) {
        FishEncyclopediaEntity entity = new FishEncyclopediaEntity();
        entity.setName(dto.getName());
        entity.setAlias(dto.getAlias());
        entity.setCategory(dto.getCategory());
        entity.setProtectionLevel(dto.getProtectionLevel());
        entity.setHabits(dto.getHabits());
        entity.setEdibleValue(dto.getEdibleValue());
        entity.setImages(dto.getImages());
        entity.setIsDeleted(0);
        fishEncyclopediaMapper.insert(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateFish(Long id, FishEncyclopediaDTO dto) {
        FishEncyclopediaEntity entity = fishEncyclopediaMapper.selectById(id);
        if (entity == null || entity.getIsDeleted() == 1) {
            throw new com.fishing.exception.BusinessException("鱼类百科不存在");
        }
        entity.setName(dto.getName());
        entity.setAlias(dto.getAlias());
        entity.setCategory(dto.getCategory());
        entity.setProtectionLevel(dto.getProtectionLevel());
        entity.setHabits(dto.getHabits());
        entity.setEdibleValue(dto.getEdibleValue());
        entity.setImages(dto.getImages());
        fishEncyclopediaMapper.updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteFish(Long id) {
        FishEncyclopediaEntity entity = fishEncyclopediaMapper.selectById(id);
        if (entity == null || entity.getIsDeleted() == 1) {
            throw new com.fishing.exception.BusinessException("鱼类百科不存在");
        }
        entity.setIsDeleted(1);
        fishEncyclopediaMapper.updateById(entity);
    }

    /**
     * 将实体转换为VO，解析images JSON字符串为List
     */
    private FishManageVO convertToVO(FishEncyclopediaEntity entity) {
        FishManageVO vo = new FishManageVO();
        vo.setId(entity.getId());
        vo.setName(entity.getName());
        vo.setAlias(entity.getAlias());
        vo.setCategory(entity.getCategory());
        vo.setProtectionLevel(entity.getProtectionLevel());
        vo.setHabits(entity.getHabits());
        vo.setEdibleValue(entity.getEdibleValue());
        vo.setCreateTime(entity.getCreateTime());
        vo.setUpdateTime(entity.getUpdateTime());

        // 解析images JSON字符串为List
        vo.setImages(parseImages(entity.getImages()));

        return vo;
    }

    /**
     * 解析图片JSON字符串为List
     */
    private List<String> parseImages(String imagesJson) {
        if (!StringUtils.hasText(imagesJson)) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(imagesJson, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            log.error("解析图片JSON失败: {}", imagesJson, e);
            return Collections.emptyList();
        }
    }
}
