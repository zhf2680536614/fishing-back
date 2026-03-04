package com.fishing.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fishing.pojo.dto.GearMarketDTO;
import com.fishing.pojo.entity.GearMarketEntity;
import com.fishing.pojo.vo.GearMarketVO;

public interface GearMarketService {
    Page<GearMarketVO> page(int pageNum, int pageSize, String category, String keyword, String sortBy);
    GearMarketVO getById(Long id);
    void save(GearMarketDTO dto, Long userId);
    void update(Long id, GearMarketDTO dto, Long userId);
    void delete(Long id, Long userId);
    void updateStatus(Long id, Integer status, Long userId);
}