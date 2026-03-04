package com.fishing.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fishing.pojo.dto.GearMarketDTO;
import com.fishing.pojo.Result;
import com.fishing.pojo.vo.GearMarketVO;
import com.fishing.service.GearMarketService;
import com.fishing.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/gear-market")
@RequiredArgsConstructor
public class GearMarketController {
    private final GearMarketService gearMarketService;

    @GetMapping("/page")
    public Result<Page<GearMarketVO>> page(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "newest") String sortBy) {
        Page<GearMarketVO> page = gearMarketService.page(pageNum, pageSize, category, keyword, sortBy);
        return Result.success(page);
    }

    @GetMapping("/{id}")
    public Result<GearMarketVO> getById(@PathVariable Long id) {
        GearMarketVO vo = gearMarketService.getById(id);
        return Result.success(vo);
    }

    @PostMapping
    public Result<Void> save(@RequestBody GearMarketDTO dto, HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        log.info("发布闲置装备，用户ID：{}，标题：{}", userId, dto.getTitle());
        gearMarketService.save(dto, userId);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody GearMarketDTO dto, HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        gearMarketService.update(id, dto, userId);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        gearMarketService.delete(id, userId);
        return Result.success();
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status, HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        gearMarketService.updateStatus(id, status, userId);
        return Result.success();
    }

    @GetMapping("/detail/{id}")
    public Result<GearMarketVO> getDetail(@PathVariable String id) {
        Long gearId = Long.parseLong(id);
        GearMarketVO detail = gearMarketService.getById(gearId);
        return Result.success(detail);
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
