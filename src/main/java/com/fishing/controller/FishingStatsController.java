package com.fishing.controller;

import com.fishing.pojo.Result;
import com.fishing.pojo.vo.FishingStatsVO;
import com.fishing.service.FishingStatsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 钓鱼统计控制器
 */
@RestController
@RequestMapping("/fishing-stats")
@CrossOrigin(origins = "*")
@Slf4j
public class FishingStatsController {

    private final FishingStatsService fishingStatsService;

    public FishingStatsController(FishingStatsService fishingStatsService) {
        this.fishingStatsService = fishingStatsService;
    }

    /**
     * 获取用户钓鱼统计数据
     * @param userId 用户ID
     * @param catchTypeCode 鱼获战报类型字典项编码
     * @param airForceTypeCode 空军吐槽类型字典项编码
     * @return 钓鱼统计数据
     */
    @GetMapping("/{userId}")
    public Result<FishingStatsVO> getFishingStats(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "catch_report") String catchTypeCode,
            @RequestParam(defaultValue = "air_force") String airForceTypeCode) {
        log.info("获取用户钓鱼统计数据：userId={}, catchTypeCode={}, airForceTypeCode={}", userId, catchTypeCode, airForceTypeCode);
        FishingStatsVO statsVO = fishingStatsService.getFishingStats(userId, catchTypeCode, airForceTypeCode);
        return Result.success(statsVO);
    }
}
