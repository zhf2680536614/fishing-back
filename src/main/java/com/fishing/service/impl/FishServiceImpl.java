package com.fishing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fishing.mapper.FishEncyclopediaMapper;
import com.fishing.pojo.PageResult;
import com.fishing.pojo.dto.FishEncyclopediaDTO;
import com.fishing.pojo.entity.FishEncyclopediaEntity;
import com.fishing.pojo.query.FishEncyclopediaPageQuery;
import com.fishing.service.FishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Slf4j
public class FishServiceImpl implements FishService {
    private final FishEncyclopediaMapper fishEncyclopediaMapper;

    public FishServiceImpl(FishEncyclopediaMapper fishEncyclopediaMapper) {
        this.fishEncyclopediaMapper = fishEncyclopediaMapper;
    }

    @Override
    public PageResult<FishEncyclopediaEntity> pageFish(FishEncyclopediaPageQuery query) {
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
        PageResult<FishEncyclopediaEntity> result = new PageResult<>();
        result.setList(entityPage.getRecords());
        result.setTotal(entityPage.getTotal());
        result.setPageNum(entityPage.getCurrent());
        result.setPageSize(entityPage.getSize());
        return result;
    }

    @Override
    public FishEncyclopediaEntity getFishById(Long id) {
        return fishEncyclopediaMapper.selectById(id);
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
        entity.setImgUrl(dto.getImgUrl());
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
        entity.setImgUrl(dto.getImgUrl());
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
}
