package com.fishing.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fishing.pojo.PageResult;
import com.fishing.pojo.dto.GearMarketDTO;
import com.fishing.pojo.query.GearMarketPageQuery;
import com.fishing.pojo.Result;
import com.fishing.pojo.vo.GearMarketManageVO;
import com.fishing.pojo.vo.GearMarketVO;
import com.fishing.service.GearMarketService;
import com.fishing.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

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
            @RequestParam(required = false) String categoryDictItemCode,
            @RequestParam(required = false) String statusDictItemCode,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "newest") String sortBy) {
        Page<GearMarketVO> page = gearMarketService.page(pageNum, pageSize, categoryDictItemCode, statusDictItemCode, keyword, sortBy);
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
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam String statusDictItemCode, HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        gearMarketService.updateStatus(id, statusDictItemCode, userId);
        return Result.success();
    }

    @GetMapping("/detail/{id}")
    public Result<GearMarketVO> getDetail(@PathVariable String id) {
        Long gearId = Long.parseLong(id);
        GearMarketVO detail = gearMarketService.getById(gearId);
        return Result.success(detail);
    }

    @GetMapping("/user/list")
    public Result<List<GearMarketVO>> getUserGearList(HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        List<GearMarketVO> list = gearMarketService.getUserGearList(userId);
        return Result.success(list);
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

    // ==================== 管理后台接口 ====================

    /**
     * 分页查询装备交易列表（管理后台）
     */
    @PostMapping("/manage/page")
    public Result<PageResult<GearMarketManageVO>> managePage(@RequestBody GearMarketPageQuery query) {
        log.info("分页查询装备交易列表（管理后台）：{}", query);
        PageResult<GearMarketManageVO> pageResult = gearMarketService.page(query);
        return Result.success(pageResult);
    }

    /**
     * 根据ID获取装备交易管理详情
     */
    @GetMapping("/manage/{id}")
    public Result<GearMarketManageVO> getGearMarketManageById(@PathVariable Long id) {
        log.info("获取装备交易管理详情：{}", id);
        GearMarketManageVO vo = gearMarketService.getGearMarketManageById(id);
        if (vo == null) {
            return Result.error("装备不存在");
        }
        return Result.success(vo);
    }

    /**
     * 保存装备交易（管理后台）
     */
    @PostMapping("/manage")
    public Result<Void> saveGearMarket(@RequestBody GearMarketDTO dto) {
        log.info("保存装备交易（管理后台）：{}", dto.getTitle());
        gearMarketService.saveGearMarket(dto);
        return Result.success();
    }

    /**
     * 更新装备交易（管理后台）
     */
    @PutMapping("/manage/{id}")
    public Result<Void> updateGearMarket(@PathVariable Long id, @RequestBody GearMarketDTO dto) {
        log.info("更新装备交易（管理后台）：{}", id);
        gearMarketService.updateGearMarket(id, dto);
        return Result.success();
    }

    /**
     * 删除装备交易（管理后台）
     */
    @DeleteMapping("/manage/{id}")
    public Result<Void> deleteGearMarket(@PathVariable Long id) {
        log.info("删除装备交易（管理后台）：{}", id);
        gearMarketService.deleteGearMarket(id);
        return Result.success();
    }
}
