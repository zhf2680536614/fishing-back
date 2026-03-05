package com.fishing.controller;

import com.fishing.pojo.Result;
import com.fishing.pojo.dto.PostCreateDTO;
import com.fishing.pojo.vo.PostVO;
import com.fishing.service.PostService;
import com.fishing.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/post")
@CrossOrigin(origins = "*")
@Slf4j
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    /**
     * 创建帖子
     */
    @PostMapping("/create")
    public Result<PostVO> createPost(@RequestBody PostCreateDTO dto, HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        log.info("创建帖子请求，用户ID：{}，标题：{}", userId, dto.getTitle());
        PostVO postVO = postService.createPost(dto, userId);
        return Result.success(postVO);
    }

    /**
     * 获取战报帖子列表
     */
    @GetMapping("/list")
    public Result<List<PostVO>> getPostList(
            @RequestParam(defaultValue = "catch_report") String typeDictItemCode,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        log.info("获取帖子列表，类型：{}，页码：{}，每页数量：{}", typeDictItemCode, pageNum, pageSize);
        List<PostVO> list = postService.getPostList(typeDictItemCode, pageNum, pageSize);
        return Result.success(list);
    }

    /**
     * 增加浏览量 - 必须放在 /{id} 之前，避免路由冲突
     */
    @PostMapping("/view/{id}")
    public Result<Void> incrementView(@PathVariable Long id) {
        log.info("增加浏览量，帖子ID：{}", id);
        postService.incrementViewCount(id);
        return Result.success();
    }

    /**
     * 点赞/取消点赞
     */
    @PostMapping("/like/{id}")
    public Result<Boolean> toggleLike(@PathVariable Long id, HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        log.info("点赞/取消点赞，帖子ID：{}，用户ID：{}", id, userId);
        Boolean isLiked = postService.toggleLike(id, userId);
        return Result.success(isLiked);
    }

    /**
     * 获取帖子详情 - 必须放在最后，避免与其他路由冲突
     */
    @GetMapping("/{id}")
    public Result<PostVO> getPostDetail(@PathVariable Long id) {
        log.info("获取帖子详情，ID：{}", id);
        PostVO postVO = postService.getPostDetail(id);
        if (postVO == null) {
            return Result.error("帖子不存在");
        }
        // 增加浏览量
        postService.incrementViewCount(id);
        return Result.success(postVO);
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
}
