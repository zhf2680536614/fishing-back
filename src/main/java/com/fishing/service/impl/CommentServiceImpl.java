package com.fishing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fishing.mapper.CommentMapper;
import com.fishing.mapper.PostMapper;
import com.fishing.mapper.UserMapper;
import com.fishing.pojo.dto.CommentCreateDTO;
import com.fishing.pojo.entity.CommentEntity;
import com.fishing.pojo.entity.PostEntity;
import com.fishing.pojo.entity.UserEntity;
import com.fishing.pojo.vo.CommentVO;
import com.fishing.service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CommentServiceImpl extends ServiceImpl<CommentMapper, CommentEntity> implements CommentService {

    private final CommentMapper commentMapper;
    private final UserMapper userMapper;
    private final PostMapper postMapper;

    public CommentServiceImpl(CommentMapper commentMapper, UserMapper userMapper, PostMapper postMapper) {
        this.commentMapper = commentMapper;
        this.userMapper = userMapper;
        this.postMapper = postMapper;
    }

    @Override
    @Transactional
    public CommentVO createComment(CommentCreateDTO dto, Long userId) {
        log.info("创建评论，用户ID：{}，帖子ID：{}，内容：{}", userId, dto.getPostId(), dto.getContent());

        CommentEntity entity = new CommentEntity();
        entity.setPostId(dto.getPostId());
        entity.setUserId(userId);
        entity.setParentId(dto.getParentId() != null ? dto.getParentId() : 0L);
        entity.setContent(dto.getContent());
        entity.setIsAiGenerated(0);

        commentMapper.insert(entity);

        PostEntity post = postMapper.selectById(dto.getPostId());
        if (post != null) {
            post.setCommentCount(post.getCommentCount() + 1);
            postMapper.updateById(post);
        }

        return convertToVO(entity);
    }

    @Override
    public List<CommentVO> getCommentList(Long postId) {
        List<CommentEntity> list = commentMapper.selectList(
                new LambdaQueryWrapper<CommentEntity>()
                        .eq(CommentEntity::getPostId, postId)
                        .eq(CommentEntity::getIsDeleted, 0)
                        .orderByAsc(CommentEntity::getCreateTime)
        );

        return list.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteComment(Long id, Long userId) {
        CommentEntity comment = commentMapper.selectById(id);
        if (comment == null || comment.getIsDeleted() == 1) {
            throw new RuntimeException("评论不存在");
        }

        if (!comment.getUserId().equals(userId)) {
            throw new RuntimeException("无权删除该评论");
        }

        comment.setIsDeleted(1);
        commentMapper.updateById(comment);

        PostEntity post = postMapper.selectById(comment.getPostId());
        if (post != null) {
            post.setCommentCount(Math.max(0, post.getCommentCount() - 1));
            postMapper.updateById(post);
        }
    }

    private CommentVO convertToVO(CommentEntity entity) {
        CommentVO vo = new CommentVO();
        BeanUtils.copyProperties(entity, vo);
        vo.setIsAiGenerated(entity.getIsAiGenerated() == 1);

        UserEntity user = userMapper.selectById(entity.getUserId());
        if (user != null) {
            vo.setUserNickname(user.getNickname());
            vo.setUserAvatar(user.getAvatar());
        }

        return vo;
    }
}
