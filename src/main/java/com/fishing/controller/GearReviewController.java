package com.fishing.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fishing.pojo.dto.GearReviewDTO;
import com.fishing.pojo.Result;
import com.fishing.pojo.vo.GearReviewVO;
import com.fishing.service.GearReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/gear-review")
@RequiredArgsConstructor
public class GearReviewController {
    private final GearReviewService gearReviewService;

    @GetMapping("/page")
    public Result<Page<GearReviewVO>> page(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword) {
        Page<GearReviewVO> page = gearReviewService.page(pageNum, pageSize, category, keyword);
        return Result.success(page);
    }

    @GetMapping("/{id}")
    public Result<GearReviewVO> getById(@PathVariable Long id) {
        GearReviewVO vo = gearReviewService.getById(id);
        return Result.success(vo);
    }

    @PostMapping
    public Result<Void> save(@RequestBody GearReviewDTO dto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        gearReviewService.save(dto, userId);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody GearReviewDTO dto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        gearReviewService.update(id, dto, userId);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        gearReviewService.delete(id, userId);
        return Result.success();
    }

    @PostMapping("/{id}/like")
    public Result<Void> like(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        gearReviewService.like(id, userId);
        return Result.success();
    }

    @DeleteMapping("/{id}/like")
    public Result<Void> unlike(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        gearReviewService.unlike(id, userId);
        return Result.success();
    }

    @GetMapping("/{id}/is-liked")
    public Result<Boolean> isLiked(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        boolean isLiked = gearReviewService.isLiked(id, userId);
        return Result.success(isLiked);
    }
}