package com.fishing.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fishing.pojo.dto.GearMarketDTO;
import com.fishing.pojo.Result;
import com.fishing.pojo.vo.GearMarketVO;
import com.fishing.service.GearMarketService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

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
            @RequestParam(required = false) String keyword) {
        Page<GearMarketVO> page = gearMarketService.page(pageNum, pageSize, category, keyword);
        return Result.success(page);
    }

    @GetMapping("/{id}")
    public Result<GearMarketVO> getById(@PathVariable Long id) {
        GearMarketVO vo = gearMarketService.getById(id);
        return Result.success(vo);
    }

    @PostMapping
    public Result<Void> save(@RequestBody GearMarketDTO dto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        gearMarketService.save(dto, userId);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody GearMarketDTO dto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        gearMarketService.update(id, dto, userId);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        gearMarketService.delete(id, userId);
        return Result.success();
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        gearMarketService.updateStatus(id, status, userId);
        return Result.success();
    }
}