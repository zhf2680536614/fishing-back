package com.fishing.controller;

import com.fishing.pojo.PageResult;
import com.fishing.pojo.Result;
import com.fishing.pojo.dto.FishingSpotCreateDTO;
import com.fishing.pojo.dto.FishingSpotUpdateDTO;
import com.fishing.pojo.query.FishingSpotPageQuery;
import com.fishing.pojo.vo.FishingSpotManageVO;
import com.fishing.pojo.vo.FishingSpotVO;
import com.fishing.service.FishingSpotService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/fishing-spot")
@CrossOrigin(origins = "*")
@Slf4j
public class FishingSpotController {

    private final FishingSpotService fishingSpotService;

    public FishingSpotController(FishingSpotService fishingSpotService) {
        this.fishingSpotService = fishingSpotService;
    }

    /**
     * 获取钓点列表
     */
    @GetMapping("/list")
    public Result<List<FishingSpotVO>> getSpotList() {
        List<FishingSpotVO> spots = fishingSpotService.getSpotList();
        return Result.success(spots);
    }

    /**
     * 获取推荐钓点（随机8个）
     */
    @GetMapping("/recommend")
    public Result<List<FishingSpotVO>> getRecommendSpots(
            @RequestParam(required = false) String typeDictItemCode) {
        List<FishingSpotVO> spots = fishingSpotService.getRecommendSpots(8, typeDictItemCode);
        return Result.success(spots);
    }

    /**
     * 搜索钓点
     */
    @GetMapping("/search")
    public Result<List<FishingSpotVO>> searchSpots(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String typeDictItemCode) {
        List<FishingSpotVO> spots = fishingSpotService.searchSpots(keyword, typeDictItemCode);
        return Result.success(spots);
    }

    /**
     * 获取钓点详情
     */
    @GetMapping("/{id}")
    public Result<FishingSpotVO> getSpotById(@PathVariable Long id) {
        FishingSpotVO spot = fishingSpotService.getSpotById(id);
        return Result.success(spot);
    }

    /**
     * 获取AI推荐
     */
    @GetMapping("/{id}/ai-recommendation")
    public Result<String> getAiRecommendation(@PathVariable Long id) {
        String recommendation = fishingSpotService.getAiRecommendation(id);
        return Result.success(recommendation);
    }

    /**
     * 分页查询钓点列表（管理后台）
     */
    @PostMapping("/page")
    public Result<PageResult<FishingSpotManageVO>> page(@RequestBody FishingSpotPageQuery query) {
        log.info("分页查询钓点列表：{}", query);
        PageResult<FishingSpotManageVO> pageResult = fishingSpotService.page(query);
        return Result.success(pageResult);
    }

    /**
     * 获取钓点管理详情
     */
    @GetMapping("/manage/{id}")
    public Result<FishingSpotManageVO> getSpotManageById(@PathVariable Long id) {
        log.info("获取钓点管理详情：{}", id);
        FishingSpotManageVO vo = fishingSpotService.getSpotManageById(id);
        return Result.success(vo);
    }

    /**
     * 创建钓点
     */
    @PostMapping
    public Result<Void> createSpot(@RequestBody FishingSpotCreateDTO dto) {
        log.info("创建钓点：{}", dto.getName());
        fishingSpotService.createSpot(dto);
        return Result.success();
    }

    /**
     * 更新钓点
     */
    @PutMapping("/{id}")
    public Result<Void> updateSpot(@PathVariable Long id, @RequestBody FishingSpotUpdateDTO dto) {
        log.info("更新钓点：{}", id);
        fishingSpotService.updateSpot(id, dto);
        return Result.success();
    }

    /**
     * 删除钓点
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteSpot(@PathVariable Long id) {
        log.info("删除钓点：{}", id);
        fishingSpotService.deleteSpot(id);
        return Result.success();
    }
}