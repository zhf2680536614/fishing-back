package com.fishing.controller;

import com.fishing.pojo.PageResult;
import com.fishing.pojo.Result;
import com.fishing.pojo.dto.CommentCreateDTO;
import com.fishing.pojo.query.CommentPageQuery;
import com.fishing.pojo.vo.CommentManageVO;
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

    // ==================== 管理后台接口 ====================

    /**
     * 分页查询评论列表（管理后台）
     */
    @PostMapping("/manage/page")
    public Result<PageResult<CommentManageVO>> managePage(@RequestBody CommentPageQuery query) {
        log.info("分页查询评论列表（管理后台）：{}", query);
        PageResult<CommentManageVO> pageResult = commentService.page(query);
        return Result.success(pageResult);
    }

    /**
     * 根据ID获取评论管理详情
     */
    @GetMapping("/manage/{id}")
    public Result<CommentManageVO> getCommentManageById(@PathVariable Long id) {
        log.info("获取评论管理详情：{}", id);
        CommentManageVO vo = commentService.getCommentManageById(id);
        if (vo == null) {
            return Result.error("评论不存在");
        }
        return Result.success(vo);
    }

    /**
     * 删除评论（管理后台）
     */
    @DeleteMapping("/manage/{id}")
    public Result<Void> deleteCommentManage(@PathVariable Long id) {
        log.info("删除评论（管理后台）：{}", id);
        commentService.deleteCommentManage(id);
        return Result.success();
    }
}
