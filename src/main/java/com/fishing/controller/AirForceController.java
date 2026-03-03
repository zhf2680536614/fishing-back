package com.fishing.controller;

import com.fishing.pojo.Result;
import com.fishing.pojo.dto.AirForceCheckinDTO;
import com.fishing.pojo.vo.AirForcePostVO;
import com.fishing.pojo.vo.AirForceStatsVO;
import com.fishing.service.AirForceService;
import com.fishing.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 空军控制器
 */
@RestController
@RequestMapping("/air-force")
@RequiredArgsConstructor
@Slf4j
public class AirForceController {

    private final AirForceService airForceService;

    /**
     * 空军打卡
     */
    @PostMapping("/checkin")
    public Result<AirForcePostVO> checkin(@RequestBody AirForceCheckinDTO dto, HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        log.info("空军打卡请求，用户ID：{}", userId);
        AirForcePostVO vo = airForceService.checkin(dto, userId);
        return Result.success(vo);
    }

    /**
     * 获取空军帖子列表
     */
    @GetMapping("/posts")
    public Result<List<AirForcePostVO>> getPostList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(defaultValue = "newest") String sortType,
            HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        log.info("获取空军帖子列表，页码：{}，每页大小：{}，排序：{}，用户ID：{}", pageNum, pageSize, sortType, userId);
        List<AirForcePostVO> list = airForceService.getPostList(pageNum, pageSize, sortType, userId);
        return Result.success(list);
    }

    /**
     * 获取空军统计数据
     */
    @GetMapping("/stats")
    public Result<AirForceStatsVO> getStats(HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        log.info("获取空军统计数据，用户ID：{}", userId);
        AirForceStatsVO stats = airForceService.getStats(userId);
        return Result.success(stats);
    }

    /**
     * 点赞/取消点赞
     */
    @PostMapping("/like/{postId}")
    public Result<Boolean> toggleLike(@PathVariable Long postId, HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        log.info("点赞/取消点赞，帖子ID：{}，用户ID：{}", postId, userId);
        Boolean isLiked = airForceService.toggleLike(postId, userId);
        return Result.success(isLiked);
    }

    /**
     * 从请求中获取当前用户ID
     */
    private Long getCurrentUserId(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            return JwtUtils.getUserIdFromToken(token);
        }
        return null;
    }
}
