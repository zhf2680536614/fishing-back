package com.fishing.controller;

import com.fishing.model.FishingResponse;
import com.fishing.model.WeatherRequest;
import com.fishing.service.FishingService;
import org.springframework.web.bind.annotation.*;

/**
 * 垂钓控制器
 */
@RestController
@RequestMapping("/fishing")
@CrossOrigin(origins = "*") // 允许跨域请求
public class FishingController {
    
    private final FishingService fishingService;

    public FishingController(FishingService fishingService) {
        this.fishingService = fishingService;
    }

    /**
     * 获取垂钓指数
     * @param weatherRequest 天气请求
     * @return 垂钓指数响应
     */
    @PostMapping("/index")
    public FishingResponse getFishingIndex(@RequestBody WeatherRequest weatherRequest) {
        return fishingService.getFishingIndex(weatherRequest);
    }
}
