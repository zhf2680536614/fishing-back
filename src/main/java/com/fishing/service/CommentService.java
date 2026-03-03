package com.fishing.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fishing.pojo.dto.CommentCreateDTO;
import com.fishing.pojo.entity.CommentEntity;
import com.fishing.pojo.vo.CommentVO;

import java.util.List;

public interface CommentService extends IService<CommentEntity> {
    CommentVO createComment(CommentCreateDTO dto, Long userId);
    List<CommentVO> getCommentList(Long postId);
    void deleteComment(Long id, Long userId);
}
