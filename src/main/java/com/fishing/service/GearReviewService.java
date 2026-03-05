package com.fishing.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fishing.pojo.PageResult;
import com.fishing.pojo.dto.GearReviewDTO;
import com.fishing.pojo.query.GearReviewPageQuery;
import com.fishing.pojo.vo.GearReviewManageVO;
import com.fishing.pojo.vo.GearReviewVO;

public interface GearReviewService {
    Page<GearReviewVO> page(int pageNum, int pageSize, String categoryDictItemCode, String statusDictItemCode, String keyword);
    GearReviewVO getById(Long id);
    void save(GearReviewDTO dto, Long userId);
    void update(Long id, GearReviewDTO dto, Long userId);
    void delete(Long id, Long userId);

    /**
     * 分页查询装备测评列表（管理后台）
     */
    PageResult<GearReviewManageVO> page(GearReviewPageQuery query);

    /**
     * 根据ID获取装备测评管理详情
     */
    GearReviewManageVO getGearReviewManageById(Long id);

    /**
     * 更新装备测评（管理后台）
     */
    void updateGearReview(Long id, GearReviewDTO dto);

    /**
     * 删除装备测评（管理后台）
     */
    void deleteGearReview(Long id);
}