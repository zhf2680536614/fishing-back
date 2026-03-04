package com.fishing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fishing.exception.BusinessException;
import com.fishing.mapper.GearMarketMapper;
import com.fishing.mapper.UserMapper;
import com.fishing.pojo.dto.GearMarketDTO;
import com.fishing.pojo.entity.GearMarketEntity;
import com.fishing.pojo.entity.UserEntity;
import com.fishing.pojo.vo.GearMarketVO;
import com.fishing.service.GearMarketService;
import com.fishing.utils.MinioUtils;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GearMarketServiceImpl implements GearMarketService {
    private final GearMarketMapper gearMarketMapper;
    private final UserMapper userMapper;
    private final MinioUtils minioUtils;
    private final Gson gson = new Gson();

    @Override
    public Page<GearMarketVO> page(int pageNum, int pageSize, String category, String keyword, String sortBy) {
        LambdaQueryWrapper<GearMarketEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(GearMarketEntity::getIsDeleted, 0);
        queryWrapper.eq(GearMarketEntity::getStatus, 0);
        if (category != null && !category.equals("all")) {
            queryWrapper.eq(GearMarketEntity::getCategory, category);
        }
        if (keyword != null && !keyword.isEmpty()) {
            queryWrapper.like(GearMarketEntity::getTitle, keyword)
                    .or().like(GearMarketEntity::getDescription, keyword);
        }
        
        // 排序逻辑
        if ("price_asc".equals(sortBy)) {
            queryWrapper.orderByAsc(GearMarketEntity::getPrice);
        } else if ("price_desc".equals(sortBy)) {
            queryWrapper.orderByDesc(GearMarketEntity::getPrice);
        } else {
            // 默认按最新发布排序
            queryWrapper.orderByDesc(GearMarketEntity::getCreateTime);
        }

        Page<GearMarketEntity> page = gearMarketMapper.selectPage(new Page<>(pageNum, pageSize), queryWrapper);
        Page<GearMarketVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());

        List<GearMarketVO> records = page.getRecords().stream().map(this::entityToVO).collect(Collectors.toList());
        voPage.setRecords(records);
        return voPage;
    }

    @Override
    public GearMarketVO getById(Long id) {
        GearMarketEntity entity = gearMarketMapper.selectById(id);
        if (entity == null || entity.getIsDeleted() == 1) {
            throw new BusinessException("装备不存在");
        }
        return entityToVO(entity);
    }

    @Override
    public void save(GearMarketDTO dto, Long userId) {
        GearMarketEntity entity = new GearMarketEntity();
        entity.setUserId(userId);
        entity.setTitle(dto.getTitle());
        entity.setDescription(dto.getDescription());
        entity.setPrice(dto.getPrice());
        entity.setOriginalPrice(dto.getOriginalPrice());
        entity.setImages(gson.toJson(dto.getImages()));
        entity.setCategory(dto.getCategory());
        entity.setStatus(0);
        entity.setIsDeleted(0);
        gearMarketMapper.insert(entity);
    }

    @Override
    public void update(Long id, GearMarketDTO dto, Long userId) {
        GearMarketEntity entity = gearMarketMapper.selectById(id);
        if (entity == null || entity.getIsDeleted() == 1) {
            throw new BusinessException("装备不存在");
        }
        if (!entity.getUserId().equals(userId)) {
            throw new BusinessException("无权修改");
        }
        entity.setTitle(dto.getTitle());
        entity.setDescription(dto.getDescription());
        entity.setPrice(dto.getPrice());
        entity.setOriginalPrice(dto.getOriginalPrice());
        entity.setImages(gson.toJson(dto.getImages()));
        entity.setCategory(dto.getCategory());
        gearMarketMapper.updateById(entity);
    }

    @Override
    public void delete(Long id, Long userId) {
        GearMarketEntity entity = gearMarketMapper.selectById(id);
        if (entity == null || entity.getIsDeleted() == 1) {
            throw new BusinessException("装备不存在");
        }
        if (!entity.getUserId().equals(userId)) {
            throw new BusinessException("无权删除");
        }
        
        // 使用 LambdaUpdateWrapper 明确更新 is_deleted 字段
        LambdaUpdateWrapper<GearMarketEntity> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(GearMarketEntity::getId, id);
        updateWrapper.set(GearMarketEntity::getIsDeleted, 1);
        gearMarketMapper.update(null, updateWrapper);
    }

    @Override
    public void updateStatus(Long id, Integer status, Long userId) {
        GearMarketEntity entity = gearMarketMapper.selectById(id);
        if (entity == null || entity.getIsDeleted() == 1) {
            throw new BusinessException("装备不存在");
        }
        if (!entity.getUserId().equals(userId)) {
            throw new BusinessException("无权修改");
        }
        entity.setStatus(status);
        gearMarketMapper.updateById(entity);
    }

    @Override
    public List<GearMarketVO> getUserGearList(Long userId) {
        LambdaQueryWrapper<GearMarketEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(GearMarketEntity::getUserId, userId);
        queryWrapper.eq(GearMarketEntity::getIsDeleted, 0);
        queryWrapper.orderByDesc(GearMarketEntity::getCreateTime);
        
        List<GearMarketEntity> entities = gearMarketMapper.selectList(queryWrapper);
        return entities.stream().map(this::entityToVO).collect(Collectors.toList());
    }

    private GearMarketVO entityToVO(GearMarketEntity entity) {
        GearMarketVO vo = new GearMarketVO();
        vo.setId(entity.getId());
        vo.setUserId(entity.getUserId());
        
        UserEntity user = userMapper.selectById(entity.getUserId());
        if (user != null) {
            vo.setUsername(user.getUsername());
            vo.setNickname(user.getNickname());
            vo.setAvatar(minioUtils.getFullUrl(user.getAvatar(), "user_avatar"));
            vo.setPhone(user.getPhone());
        }
        
        vo.setTitle(entity.getTitle());
        vo.setDescription(entity.getDescription());
        vo.setPrice(entity.getPrice());
        vo.setOriginalPrice(entity.getOriginalPrice());
        vo.setImages(gson.fromJson(entity.getImages(), List.class));
        vo.setCategory(entity.getCategory());
        vo.setStatus(entity.getStatus());
        vo.setCreateTime(entity.getCreateTime());
        return vo;
    }
}