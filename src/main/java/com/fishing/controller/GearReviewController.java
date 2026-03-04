package com.fishing.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fishing.pojo.dto.GearReviewDTO;
import com.fishing.pojo.Result;
import com.fishing.pojo.vo.GearReviewVO;
import com.fishing.service.GearReviewService;
import com.fishing.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

@Slf4j
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
    public Result<GearReviewVO> getById(@PathVariable String id) {
        Long reviewId = Long.parseLong(id);
        GearReviewVO vo = gearReviewService.getById(reviewId);
        return Result.success(vo);
    }

    @PostMapping
    public Result<Void> save(@RequestBody GearReviewDTO dto, HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        log.info("发布装备测评，用户ID：{}，标题：{}", userId, dto.getTitle());
        gearReviewService.save(dto, userId);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody GearReviewDTO dto, HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        gearReviewService.update(id, dto, userId);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        gearReviewService.delete(id, userId);
        return Result.success();
    }

    private Long getCurrentUserId(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            Long userId = JwtUtils.getUserIdFromToken(token);
            if (userId != null) {
                return userId;
            }
        }
        return 1L;
    }
}
