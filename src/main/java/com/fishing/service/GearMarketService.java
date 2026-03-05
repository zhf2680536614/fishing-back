package com.fishing.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fishing.pojo.dto.GearMarketDTO;
import com.fishing.pojo.PageResult;
import com.fishing.pojo.query.GearMarketPageQuery;
import com.fishing.pojo.vo.GearMarketManageVO;
import com.fishing.pojo.vo.GearMarketVO;

import java.util.List;

public interface GearMarketService {
    Page<GearMarketVO> page(int pageNum, int pageSize, String categoryDictItemCode, String statusDictItemCode, String keyword, String sortBy);

    GearMarketVO getById(Long id);

    void save(GearMarketDTO dto, Long userId);

    void update(Long id, GearMarketDTO dto, Long userId);

    void delete(Long id, Long userId);

    void updateStatus(Long id, String statusDictItemCode, Long userId);

    List<GearMarketVO> getUserGearList(Long userId);

    /**
     * 分页查询装备交易列表（管理后台）
     */
    PageResult<GearMarketManageVO> page(GearMarketPageQuery query);

    /**
     * 根据ID获取装备交易管理详情
     */
    GearMarketManageVO getGearMarketManageById(Long id);

    /**
     * 保存装备交易（管理后台）
     */
    void saveGearMarket(GearMarketDTO dto);

    /**
     * 更新装备交易（管理后台）
     */
    void updateGearMarket(Long id, GearMarketDTO dto);

    /**
     * 删除装备交易（管理后台）
     */
    void deleteGearMarket(Long id);
}