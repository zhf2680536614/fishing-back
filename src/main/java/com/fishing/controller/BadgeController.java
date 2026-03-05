package com.fishing.controller;

import com.fishing.pojo.Result;
import com.fishing.pojo.PageResult;
import com.fishing.pojo.dto.BadgeDTO;
import com.fishing.pojo.query.BadgePageQuery;
import com.fishing.pojo.vo.BadgeManageVO;
import com.fishing.service.BadgeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/badge")
@CrossOrigin(origins = "*")
@Slf4j
public class BadgeController {

    private final BadgeService badgeService;

    public BadgeController(BadgeService badgeService) {
        this.badgeService = badgeService;
    }

    /**
     * 分页查询勋章列表（管理后台）
     */
    @PostMapping("/manage/page")
    public Result<PageResult<BadgeManageVO>> managePage(@RequestBody BadgePageQuery query) {
        log.info("分页查询勋章列表（管理后台）：{}", query);
        PageResult<BadgeManageVO> pageResult = badgeService.page(query);
        return Result.success(pageResult);
    }

    /**
     * 根据ID获取勋章详情（管理后台）
     */
    @GetMapping("/manage/{id}")
    public Result<BadgeManageVO> getBadgeManageById(@PathVariable Long id) {
        log.info("获取勋章详情（管理后台）：{}", id);
        BadgeManageVO vo = badgeService.getBadgeManageById(id);
        if (vo == null) {
            return Result.error("勋章不存在");
        }
        return Result.success(vo);
    }

    /**
     * 创建勋章（管理后台）
     */
    @PostMapping("/manage")
    public Result<Void> createBadge(@RequestBody BadgeDTO dto) {
        log.info("创建勋章（管理后台）：{}", dto);
        badgeService.createBadge(dto);
        return Result.success();
    }

    /**
     * 更新勋章（管理后台）
     */
    @PutMapping("/manage/{id}")
    public Result<Void> updateBadge(@PathVariable Long id, @RequestBody BadgeDTO dto) {
        log.info("更新勋章（管理后台）：{}，数据：{}", id, dto);
        badgeService.updateBadge(id, dto);
        return Result.success();
    }

    /**
     * 删除勋章（管理后台）
     */
    @DeleteMapping("/manage/{id}")
    public Result<Void> deleteBadge(@PathVariable Long id) {
        log.info("删除勋章（管理后台）：{}", id);
        badgeService.deleteBadge(id);
        return Result.success();
    }
}
