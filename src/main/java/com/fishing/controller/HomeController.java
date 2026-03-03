package com.fishing.controller;

import com.fishing.pojo.Result;
import com.fishing.pojo.vo.HomeStatsVO;
import com.fishing.pojo.vo.HotPostVO;
import com.fishing.service.HomeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 首页控制器
 */
@RestController
@RequestMapping("/home")
@CrossOrigin(origins = "*")
@Slf4j
public class HomeController {

    private final HomeService homeService;

    public HomeController(HomeService homeService) {
        this.homeService = homeService;
    }

    /**
     * 获取首页统计数据
     */
    @GetMapping("/stats")
    public Result<HomeStatsVO> getHomeStats() {
        log.info("获取首页统计数据");
        HomeStatsVO stats = homeService.getHomeStats();
        return Result.success(stats);
    }

    /**
     * 获取今日爆护榜
     */
    @GetMapping("/hot-posts")
    public Result<List<HotPostVO>> getTodayHotPosts() {
        log.info("获取今日爆护榜");
        List<HotPostVO> list = homeService.getTodayHotPosts();
        return Result.success(list);
    }
}
