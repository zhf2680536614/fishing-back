package com.fishing.controller;

import com.fishing.pojo.Result;
import com.fishing.pojo.vo.FishVO;
import com.fishing.service.FishEncyclopediaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/ai")
@Slf4j
public class FishEncyclopediaController {

    private final FishEncyclopediaService fishEncyclopediaService;

    public FishEncyclopediaController(FishEncyclopediaService fishEncyclopediaService) {
        this.fishEncyclopediaService = fishEncyclopediaService;
    }

    @PostMapping("/identify-fish")
    public Result<FishVO> identifyFish(@RequestParam("image") MultipartFile image) {
        log.info("收到 AI 识鱼请求");
        try {
            FishVO fishVO = fishEncyclopediaService.identifyFish(image);
            return Result.success(fishVO);
        } catch (Exception e) {
            log.error("AI 识鱼失败", e);
            return Result.error(500, e.getMessage());
        }
    }
}
