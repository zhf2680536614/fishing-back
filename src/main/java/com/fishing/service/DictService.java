package com.fishing.service;

import com.fishing.pojo.dto.DictItemDTO;
import com.fishing.pojo.dto.DictTypeDTO;
import com.fishing.pojo.entity.DictItemEntity;
import com.fishing.pojo.entity.DictTypeEntity;
import com.fishing.pojo.PageResult;
import com.fishing.pojo.query.DictItemPageQuery;
import com.fishing.pojo.query.DictTypePageQuery;

import java.util.List;

public interface DictService {
    List<DictItemEntity> getDictItemsByCode(String dictCode);

    /**
     * 根据字典类型编码和字典项编码获取字典项名称
     */
    String getItemName(String dictTypeCode, String itemCode);

    /**
     * 分页查询字典类型列表（管理后台）
     */
    PageResult<DictTypeEntity> pageDictTypes(DictTypePageQuery query);

    /**
     * 获取所有字典类型（用于下拉选择）
     */
    List<DictTypeEntity> getAllDictTypes();

    /**
     * 根据ID获取字典类型详情
     */
    DictTypeEntity getDictTypeById(Long id);

    /**
     * 创建字典类型
     */
    void createDictType(DictTypeDTO dto);

    /**
     * 更新字典类型
     */
    void updateDictType(Long id, DictTypeDTO dto);

    /**
     * 删除字典类型
     */
    void deleteDictType(Long id);

    /**
     * 分页查询字典项列表（管理后台）
     */
    PageResult<DictItemEntity> pageDictItems(DictItemPageQuery query);

    /**
     * 根据ID获取字典项详情
     */
    DictItemEntity getDictItemById(Long id);

    /**
     * 创建字典项
     */
    void createDictItem(DictItemDTO dto);

    /**
     * 更新字典项
     */
    void updateDictItem(Long id, DictItemDTO dto);

    /**
     * 删除字典项
     */
    void deleteDictItem(Long id);
}
