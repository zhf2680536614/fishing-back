package com.fishing.controller;

import com.fishing.pojo.Result;
import com.fishing.pojo.dto.CommentCreateDTO;
import com.fishing.pojo.vo.CommentVO;
import com.fishing.service.CommentService;
import com.fishing.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comment")
@CrossOrigin(origins = "*")
@Slf4j
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/create")
    public Result<CommentVO> createComment(@RequestBody CommentCreateDTO dto, HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        log.info("创建评论请求，用户ID：{}，帖子ID：{}", userId, dto.getPostId());
        CommentVO commentVO = commentService.createComment(dto, userId);
        return Result.success(commentVO);
    }

    @GetMapping("/list/{postId}")
    public Result<List<CommentVO>> getCommentList(@PathVariable Long postId) {
        log.info("获取评论列表，帖子ID：{}", postId);
        List<CommentVO> list = commentService.getCommentList(postId);
        return Result.success(list);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteComment(@PathVariable Long id, HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        log.info("删除评论请求，评论ID：{}，用户ID：{}", id, userId);
        commentService.deleteComment(id, userId);
        return Result.success();
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
