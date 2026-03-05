package com.fishing.controller;

import com.fishing.pojo.Result;
import com.fishing.pojo.vo.FishingSpotVO;
import com.fishing.service.FishingSpotService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/fishing-spot")
@CrossOrigin(origins = "*")
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
}