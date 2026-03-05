package com.fishing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fishing.exception.BusinessException;
import com.fishing.mapper.GearMarketMapper;
import com.fishing.mapper.UserMapper;
import com.fishing.pojo.dto.GearMarketDTO;
import com.fishing.pojo.entity.GearMarketEntity;
import com.fishing.pojo.entity.UserEntity;
import com.fishing.pojo.PageResult;
import com.fishing.pojo.query.GearMarketPageQuery;
import com.fishing.pojo.vo.GearMarketManageVO;
import com.fishing.pojo.vo.GearMarketVO;
import com.fishing.service.DictService;
import com.fishing.service.GearMarketService;
import com.fishing.utils.MinioUtils;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GearMarketServiceImpl implements GearMarketService {
    private final GearMarketMapper gearMarketMapper;
    private final UserMapper userMapper;
    private final MinioUtils minioUtils;
    private final DictService dictService;
    private final Gson gson = new Gson();

    @Override
    public Page<GearMarketVO> page(int pageNum, int pageSize, String categoryDictItemCode, String statusDictItemCode, String keyword, String sortBy) {
        LambdaQueryWrapper<GearMarketEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(GearMarketEntity::getIsDeleted, 0);
        if (statusDictItemCode != null && !statusDictItemCode.isEmpty()) {
            queryWrapper.eq(GearMarketEntity::getStatusDictItemCode, statusDictItemCode);
        }
        if (categoryDictItemCode != null && !categoryDictItemCode.equals("all")) {
            queryWrapper.eq(GearMarketEntity::getCategoryDictItemCode, categoryDictItemCode);
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
        entity.setCategoryDictTypeCode(dto.getCategoryDictTypeCode());
        entity.setCategoryDictItemCode(dto.getCategoryDictItemCode());
        entity.setStatusDictTypeCode(dto.getStatusDictTypeCode());
        entity.setStatusDictItemCode(dto.getStatusDictItemCode());
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
        entity.setCategoryDictTypeCode(dto.getCategoryDictTypeCode());
        entity.setCategoryDictItemCode(dto.getCategoryDictItemCode());
        entity.setStatusDictTypeCode(dto.getStatusDictTypeCode());
        entity.setStatusDictItemCode(dto.getStatusDictItemCode());
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
    public void updateStatus(Long id, String statusDictItemCode, Long userId) {
        GearMarketEntity entity = gearMarketMapper.selectById(id);
        if (entity == null || entity.getIsDeleted() == 1) {
            throw new BusinessException("装备不存在");
        }
        if (!entity.getUserId().equals(userId)) {
            throw new BusinessException("无权修改");
        }
        entity.setStatusDictItemCode(statusDictItemCode);
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
        vo.setCategoryDictItemCode(entity.getCategoryDictItemCode());
        vo.setStatusDictItemCode(entity.getStatusDictItemCode());
        vo.setCreateTime(entity.getCreateTime());
        return vo;
    }

    @Override
    public PageResult<GearMarketManageVO> page(GearMarketPageQuery query) {
        Page<GearMarketEntity> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<GearMarketEntity> wrapper = new LambdaQueryWrapper<>();

        // 标题模糊查询
        if (StringUtils.hasText(query.getTitle())) {
            wrapper.like(GearMarketEntity::getTitle, query.getTitle());
        }

        // 分类筛选
        if (StringUtils.hasText(query.getCategoryDictItemCode())) {
            wrapper.eq(GearMarketEntity::getCategoryDictItemCode, query.getCategoryDictItemCode());
        }

        // 状态筛选
        if (StringUtils.hasText(query.getStatusDictItemCode())) {
            wrapper.eq(GearMarketEntity::getStatusDictItemCode, query.getStatusDictItemCode());
        }

        // 用户ID筛选
        if (query.getUserId() != null) {
            wrapper.eq(GearMarketEntity::getUserId, query.getUserId());
        }

        // 价格范围筛选
        if (query.getMinPrice() != null) {
            wrapper.ge(GearMarketEntity::getPrice, query.getMinPrice());
        }
        if (query.getMaxPrice() != null) {
            wrapper.le(GearMarketEntity::getPrice, query.getMaxPrice());
        }

        wrapper.eq(GearMarketEntity::getIsDeleted, 0)
                .orderByDesc(GearMarketEntity::getCreateTime);

        IPage<GearMarketEntity> entityPage = gearMarketMapper.selectPage(page, wrapper);
        List<GearMarketManageVO> voList = entityPage.getRecords().stream()
                .map(this::convertToManageVO)
                .collect(Collectors.toList());

        PageResult<GearMarketManageVO> result = new PageResult<>();
        result.setList(voList);
        result.setTotal(entityPage.getTotal());
        result.setPageNum(entityPage.getCurrent());
        result.setPageSize(entityPage.getSize());
        return result;
    }

    @Override
    public GearMarketManageVO getGearMarketManageById(Long id) {
        GearMarketEntity entity = gearMarketMapper.selectById(id);
        if (entity == null || entity.getIsDeleted() == 1) {
            return null;
        }
        return convertToManageVO(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveGearMarket(GearMarketDTO dto) {
        GearMarketEntity entity = new GearMarketEntity();
        entity.setUserId(dto.getUserId() != null ? dto.getUserId() : 1L);
        entity.setTitle(dto.getTitle());
        entity.setDescription(dto.getDescription());
        entity.setPrice(dto.getPrice());
        entity.setOriginalPrice(dto.getOriginalPrice());
        entity.setImages(gson.toJson(dto.getImages()));
        entity.setCategoryDictTypeCode(dto.getCategoryDictTypeCode());
        entity.setCategoryDictItemCode(dto.getCategoryDictItemCode());
        entity.setStatusDictTypeCode(dto.getStatusDictTypeCode());
        entity.setStatusDictItemCode(dto.getStatusDictItemCode());
        entity.setIsDeleted(0);
        gearMarketMapper.insert(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateGearMarket(Long id, GearMarketDTO dto) {
        GearMarketEntity entity = gearMarketMapper.selectById(id);
        if (entity == null || entity.getIsDeleted() == 1) {
            throw new BusinessException("装备不存在");
        }
        entity.setTitle(dto.getTitle());
        entity.setDescription(dto.getDescription());
        entity.setPrice(dto.getPrice());
        entity.setOriginalPrice(dto.getOriginalPrice());
        if (dto.getImages() != null) {
            entity.setImages(gson.toJson(dto.getImages()));
        }
        entity.setCategoryDictTypeCode(dto.getCategoryDictTypeCode());
        entity.setCategoryDictItemCode(dto.getCategoryDictItemCode());
        entity.setStatusDictTypeCode(dto.getStatusDictTypeCode());
        entity.setStatusDictItemCode(dto.getStatusDictItemCode());
        gearMarketMapper.updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteGearMarket(Long id) {
        GearMarketEntity entity = gearMarketMapper.selectById(id);
        if (entity == null || entity.getIsDeleted() == 1) {
            throw new BusinessException("装备不存在");
        }

        LambdaUpdateWrapper<GearMarketEntity> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(GearMarketEntity::getId, id);
        updateWrapper.set(GearMarketEntity::getIsDeleted, 1);
        gearMarketMapper.update(null, updateWrapper);
    }

    private GearMarketManageVO convertToManageVO(GearMarketEntity entity) {
        GearMarketManageVO vo = new GearMarketManageVO();
        BeanUtils.copyProperties(entity, vo);

        // 获取用户信息
        UserEntity user = userMapper.selectById(entity.getUserId());
        if (user != null) {
            vo.setUsername(user.getUsername());
            vo.setNickname(user.getNickname());
            vo.setAvatar(minioUtils.getFullUrl(user.getAvatar(), "user_avatar"));
            vo.setPhone(user.getPhone());
        }

        // 解析图片
        if (StringUtils.hasText(entity.getImages())) {
            vo.setImages(gson.fromJson(entity.getImages(), List.class));
        }

        // 获取分类字典项名称
        if (StringUtils.hasText(entity.getCategoryDictTypeCode()) &&
                StringUtils.hasText(entity.getCategoryDictItemCode())) {
            String itemName = dictService.getItemName(
                    entity.getCategoryDictTypeCode(),
                    entity.getCategoryDictItemCode()
            );
            vo.setCategoryDictItemName(itemName);
        }

        // 获取状态字典项名称
        if (StringUtils.hasText(entity.getStatusDictTypeCode()) &&
                StringUtils.hasText(entity.getStatusDictItemCode())) {
            String itemName = dictService.getItemName(
                    entity.getStatusDictTypeCode(),
                    entity.getStatusDictItemCode()
            );
            vo.setStatusDictItemName(itemName);
        }

        return vo;
    }
}