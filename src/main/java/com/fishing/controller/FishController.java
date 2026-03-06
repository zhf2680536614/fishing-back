package com.fishing.controller;

import com.fishing.pojo.Result;
import com.fishing.pojo.PageResult;
import com.fishing.pojo.dto.FishEncyclopediaDTO;
import com.fishing.pojo.query.FishEncyclopediaPageQuery;
import com.fishing.pojo.vo.FishManageVO;
import com.fishing.service.FishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/fish")
@CrossOrigin(origins = "*")
@Slf4j
public class FishController {
    private final FishService fishService;

    public FishController(FishService fishService) {
        this.fishService = fishService;
    }

    /**
     * 分页查询鱼类百科列表（管理后台）
     */
    @PostMapping("/manage/page")
    public Result<PageResult<FishManageVO>> pageFish(@RequestBody FishEncyclopediaPageQuery query) {
        log.info("分页查询鱼类百科列表（管理后台）：{}", query);
        PageResult<FishManageVO> pageResult = fishService.pageFish(query);
        return Result.success(pageResult);
    }

    /**
     * 根据ID获取鱼类百科详情（管理后台）
     */
    @GetMapping("/manage/{id}")
    public Result<FishManageVO> getFishById(@PathVariable Long id) {
        log.info("获取鱼类百科详情（管理后台）：{}", id);
        FishManageVO fish = fishService.getFishById(id);
        if (fish == null) {
            return Result.error("鱼类百科不存在");
        }
        return Result.success(fish);
    }

    /**
     * 创建鱼类百科（管理后台）
     */
    @PostMapping("/manage")
    public Result<Void> createFish(@RequestBody FishEncyclopediaDTO dto) {
        log.info("创建鱼类百科（管理后台）：{}", dto);
        fishService.createFish(dto);
        return Result.success();
    }

    /**
     * 更新鱼类百科（管理后台）
     */
    @PutMapping("/manage/{id}")
    public Result<Void> updateFish(@PathVariable Long id, @RequestBody FishEncyclopediaDTO dto) {
        log.info("更新鱼类百科（管理后台）：{}，数据：{}", id, dto);
        fishService.updateFish(id, dto);
        return Result.success();
    }

    /**
     * 删除鱼类百科（管理后台）
     */
    @DeleteMapping("/manage/{id}")
    public Result<Void> deleteFish(@PathVariable Long id) {
        log.info("删除鱼类百科（管理后台）：{}", id);
        fishService.deleteFish(id);
        return Result.success();
    }
}
