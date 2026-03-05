package com.fishing.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fishing.pojo.dto.CommentCreateDTO;
import com.fishing.pojo.entity.CommentEntity;
import com.fishing.pojo.PageResult;
import com.fishing.pojo.query.CommentPageQuery;
import com.fishing.pojo.vo.CommentManageVO;
import com.fishing.pojo.vo.CommentVO;

import java.util.List;

public interface CommentService extends IService<CommentEntity> {
    CommentVO createComment(CommentCreateDTO dto, Long userId);
    List<CommentVO> getCommentList(Long postId);
    void deleteComment(Long id, Long userId);

    /**
     * 分页查询评论列表（管理后台）
     */
    PageResult<CommentManageVO> page(CommentPageQuery query);

    /**
     * 根据ID获取评论管理详情
     */
    CommentManageVO getCommentManageById(Long id);

    /**
     * 删除评论（管理后台）
     */
    void deleteCommentManage(Long id);
}
