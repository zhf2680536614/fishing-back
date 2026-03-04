package com.fishing.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fishing.pojo.dto.GearReviewDTO;
import com.fishing.pojo.vo.GearReviewVO;

public interface GearReviewService {
    Page<GearReviewVO> page(int pageNum, int pageSize, String category, String keyword);
    GearReviewVO getById(Long id);
    void save(GearReviewDTO dto, Long userId);
    void update(Long id, GearReviewDTO dto, Long userId);
    void delete(Long id, Long userId);
    void like(Long reviewId, Long userId);
    void unlike(Long reviewId, Long userId);
    boolean isLiked(Long reviewId, Long userId);
}