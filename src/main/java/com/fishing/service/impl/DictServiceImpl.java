package com.fishing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fishing.mapper.DictItemMapper;
import com.fishing.mapper.DictTypeMapper;
import com.fishing.pojo.PageResult;
import com.fishing.pojo.dto.DictItemDTO;
import com.fishing.pojo.dto.DictTypeDTO;
import com.fishing.pojo.entity.DictItemEntity;
import com.fishing.pojo.entity.DictTypeEntity;
import com.fishing.pojo.query.DictItemPageQuery;
import com.fishing.pojo.query.DictTypePageQuery;
import com.fishing.service.DictService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@Slf4j
public class DictServiceImpl implements DictService {
    private final DictItemMapper dictItemMapper;
    private final DictTypeMapper dictTypeMapper;

    public DictServiceImpl(DictItemMapper dictItemMapper, DictTypeMapper dictTypeMapper) {
        this.dictItemMapper = dictItemMapper;
        this.dictTypeMapper = dictTypeMapper;
    }

    @Override
    public List<DictItemEntity> getDictItemsByCode(String dictCode) {
        log.info("获取字典项，字典编码：{}", dictCode);
        return dictItemMapper.selectByDictCode(dictCode);
    }

    @Override
    public String getItemName(String dictTypeCode, String itemCode) {
        if (dictTypeCode == null || itemCode == null) {
            return "";
        }
        // 先查询字典类型获取ID
        LambdaQueryWrapper<DictTypeEntity> typeWrapper = new LambdaQueryWrapper<>();
        typeWrapper.eq(DictTypeEntity::getDictCode, dictTypeCode);
        DictTypeEntity dictType = dictTypeMapper.selectOne(typeWrapper);
        if (dictType == null) {
            return itemCode;
        }
        // 再查询字典项
        LambdaQueryWrapper<DictItemEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DictItemEntity::getDictTypeId, dictType.getId());
        wrapper.eq(DictItemEntity::getItemCode, itemCode);
        DictItemEntity item = dictItemMapper.selectOne(wrapper);
        return item != null ? item.getItemName() : itemCode;
    }

    @Override
    public PageResult<DictTypeEntity> pageDictTypes(DictTypePageQuery query) {
        Page<DictTypeEntity> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<DictTypeEntity> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(query.getDictCode())) {
            wrapper.like(DictTypeEntity::getDictCode, query.getDictCode());
        }
        if (StringUtils.hasText(query.getDictName())) {
            wrapper.like(DictTypeEntity::getDictName, query.getDictName());
        }
        if (query.getStatus() != null) {
            wrapper.eq(DictTypeEntity::getStatus, query.getStatus());
        }
        
        wrapper.eq(DictTypeEntity::getIsDeleted, 0)
                .orderByAsc(DictTypeEntity::getSortOrder);
        
        IPage<DictTypeEntity> entityPage = dictTypeMapper.selectPage(page, wrapper);
        List<DictTypeEntity> list = entityPage.getRecords();
        
        PageResult<DictTypeEntity> result = new PageResult<>();
        result.setList(list);
        result.setTotal(entityPage.getTotal());
        result.setPageNum(entityPage.getCurrent());
        result.setPageSize(entityPage.getSize());
        return result;
    }

    @Override
    public List<DictTypeEntity> getAllDictTypes() {
        LambdaQueryWrapper<DictTypeEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DictTypeEntity::getIsDeleted, 0)
                .eq(DictTypeEntity::getStatus, 1)
                .orderByAsc(DictTypeEntity::getSortOrder);
        return dictTypeMapper.selectList(wrapper);
    }

    @Override
    public DictTypeEntity getDictTypeById(Long id) {
        return dictTypeMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createDictType(DictTypeDTO dto) {
        DictTypeEntity entity = new DictTypeEntity();
        entity.setDictCode(dto.getDictCode());
        entity.setDictName(dto.getDictName());
        entity.setParentId(dto.getParentId());
        entity.setDescription(dto.getDescription());
        entity.setSortOrder(dto.getSortOrder());
        entity.setStatus(dto.getStatus());
        entity.setIsDeleted(0);
        dictTypeMapper.insert(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDictType(Long id, DictTypeDTO dto) {
        DictTypeEntity entity = dictTypeMapper.selectById(id);
        if (entity == null || entity.getIsDeleted() == 1) {
            throw new com.fishing.exception.BusinessException("字典类型不存在");
        }
        entity.setDictCode(dto.getDictCode());
        entity.setDictName(dto.getDictName());
        entity.setParentId(dto.getParentId());
        entity.setDescription(dto.getDescription());
        entity.setSortOrder(dto.getSortOrder());
        entity.setStatus(dto.getStatus());
        dictTypeMapper.updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDictType(Long id) {
        DictTypeEntity entity = dictTypeMapper.selectById(id);
        if (entity == null || entity.getIsDeleted() == 1) {
            throw new com.fishing.exception.BusinessException("字典类型不存在");
        }
        entity.setIsDeleted(1);
        dictTypeMapper.updateById(entity);
    }

    @Override
    public PageResult<DictItemEntity> pageDictItems(DictItemPageQuery query) {
        Page<DictItemEntity> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<DictItemEntity> wrapper = new LambdaQueryWrapper<>();
        
        if (query.getDictTypeId() != null) {
            wrapper.eq(DictItemEntity::getDictTypeId, query.getDictTypeId());
        }
        if (StringUtils.hasText(query.getItemCode())) {
            wrapper.like(DictItemEntity::getItemCode, query.getItemCode());
        }
        if (StringUtils.hasText(query.getItemName())) {
            wrapper.like(DictItemEntity::getItemName, query.getItemName());
        }
        if (query.getStatus() != null) {
            wrapper.eq(DictItemEntity::getStatus, query.getStatus());
        }
        
        wrapper.eq(DictItemEntity::getIsDeleted, 0)
                .orderByAsc(DictItemEntity::getSortOrder);
        
        IPage<DictItemEntity> entityPage = dictItemMapper.selectPage(page, wrapper);
        List<DictItemEntity> list = entityPage.getRecords();
        
        PageResult<DictItemEntity> result = new PageResult<>();
        result.setList(list);
        result.setTotal(entityPage.getTotal());
        result.setPageNum(entityPage.getCurrent());
        result.setPageSize(entityPage.getSize());
        return result;
    }

    @Override
    public DictItemEntity getDictItemById(Long id) {
        return dictItemMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createDictItem(DictItemDTO dto) {
        DictItemEntity entity = new DictItemEntity();
        entity.setDictTypeId(dto.getDictTypeId());
        entity.setItemCode(dto.getItemCode());
        entity.setItemName(dto.getItemName());
        entity.setValue(dto.getValue());
        entity.setDescription(dto.getDescription());
        entity.setSortOrder(dto.getSortOrder());
        entity.setStatus(dto.getStatus());
        entity.setIsDeleted(0);
        dictItemMapper.insert(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDictItem(Long id, DictItemDTO dto) {
        DictItemEntity entity = dictItemMapper.selectById(id);
        if (entity == null || entity.getIsDeleted() == 1) {
            throw new com.fishing.exception.BusinessException("字典项不存在");
        }
        entity.setDictTypeId(dto.getDictTypeId());
        entity.setItemCode(dto.getItemCode());
        entity.setItemName(dto.getItemName());
        entity.setValue(dto.getValue());
        entity.setDescription(dto.getDescription());
        entity.setSortOrder(dto.getSortOrder());
        entity.setStatus(dto.getStatus());
        dictItemMapper.updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDictItem(Long id) {
        DictItemEntity entity = dictItemMapper.selectById(id);
        if (entity == null || entity.getIsDeleted() == 1) {
            throw new com.fishing.exception.BusinessException("字典项不存在");
        }
        entity.setIsDeleted(1);
        dictItemMapper.updateById(entity);
    }
}
