package com.fishing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fishing.mapper.CommentMapper;
import com.fishing.mapper.PostMapper;
import com.fishing.mapper.UserMapper;
import com.fishing.pojo.dto.CommentCreateDTO;
import com.fishing.pojo.entity.CommentEntity;
import com.fishing.pojo.entity.PostEntity;
import com.fishing.pojo.entity.UserEntity;
import com.fishing.pojo.PageResult;
import com.fishing.pojo.query.CommentPageQuery;
import com.fishing.pojo.vo.CommentManageVO;
import com.fishing.pojo.vo.CommentVO;
import com.fishing.service.CommentService;
import com.fishing.service.DictService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CommentServiceImpl extends ServiceImpl<CommentMapper, CommentEntity> implements CommentService {

    private final CommentMapper commentMapper;
    private final UserMapper userMapper;
    private final PostMapper postMapper;
    private final DictService dictService;

    public CommentServiceImpl(CommentMapper commentMapper, UserMapper userMapper, PostMapper postMapper, DictService dictService) {
        this.commentMapper = commentMapper;
        this.userMapper = userMapper;
        this.postMapper = postMapper;
        this.dictService = dictService;
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
        entity.setIsAiGeneratedDictTypeCode("ai_generated");
        entity.setIsAiGeneratedDictItemCode("no");

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
        vo.setIsAiGenerated("yes".equals(entity.getIsAiGeneratedDictItemCode()));

        UserEntity user = userMapper.selectById(entity.getUserId());
        if (user != null) {
            vo.setUserNickname(user.getNickname());
            vo.setUserAvatar(user.getAvatar());
        }

        return vo;
    }

    @Override
    public PageResult<CommentManageVO> page(CommentPageQuery query) {
        Page<CommentEntity> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<CommentEntity> wrapper = new LambdaQueryWrapper<>();

        // 帖子ID筛选
        if (query.getPostId() != null) {
            wrapper.eq(CommentEntity::getPostId, query.getPostId());
        }

        // 用户ID筛选
        if (query.getUserId() != null) {
            wrapper.eq(CommentEntity::getUserId, query.getUserId());
        }

        // 内容模糊查询
        if (StringUtils.hasText(query.getContent())) {
            wrapper.like(CommentEntity::getContent, query.getContent());
        }

        // 是否AI生成筛选
        if (StringUtils.hasText(query.getIsAiGeneratedDictItemCode())) {
            wrapper.eq(CommentEntity::getIsAiGeneratedDictItemCode, query.getIsAiGeneratedDictItemCode());
        }

        wrapper.eq(CommentEntity::getIsDeleted, 0)
                .orderByDesc(CommentEntity::getCreateTime);

        IPage<CommentEntity> entityPage = commentMapper.selectPage(page, wrapper);
        List<CommentManageVO> voList = entityPage.getRecords().stream()
                .map(this::convertToManageVO)
                .collect(Collectors.toList());

        PageResult<CommentManageVO> result = new PageResult<>();
        result.setList(voList);
        result.setTotal(entityPage.getTotal());
        result.setPageNum(entityPage.getCurrent());
        result.setPageSize(entityPage.getSize());
        return result;
    }

    @Override
    public CommentManageVO getCommentManageById(Long id) {
        CommentEntity entity = commentMapper.selectById(id);
        if (entity == null || entity.getIsDeleted() == 1) {
            return null;
        }
        return convertToManageVO(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCommentManage(Long id) {
        CommentEntity comment = commentMapper.selectById(id);
        if (comment == null || comment.getIsDeleted() == 1) {
            throw new RuntimeException("评论不存在");
        }

        comment.setIsDeleted(1);
        commentMapper.updateById(comment);

        // 更新帖子评论数
        PostEntity post = postMapper.selectById(comment.getPostId());
        if (post != null) {
            post.setCommentCount(Math.max(0, post.getCommentCount() - 1));
            postMapper.updateById(post);
        }
    }

    private CommentManageVO convertToManageVO(CommentEntity entity) {
        CommentManageVO vo = new CommentManageVO();
        BeanUtils.copyProperties(entity, vo);

        // 获取用户信息
        UserEntity user = userMapper.selectById(entity.getUserId());
        if (user != null) {
            vo.setUsername(user.getUsername());
            vo.setNickname(user.getNickname());
            vo.setAvatar(user.getAvatar());
        }

        // 获取帖子信息
        PostEntity post = postMapper.selectById(entity.getPostId());
        if (post != null) {
            vo.setPostTitle(post.getTitle());
        }

        // 获取字典项名称
        if (StringUtils.hasText(entity.getIsAiGeneratedDictTypeCode()) &&
                StringUtils.hasText(entity.getIsAiGeneratedDictItemCode())) {
            String itemName = dictService.getItemName(
                    entity.getIsAiGeneratedDictTypeCode(),
                    entity.getIsAiGeneratedDictItemCode()
            );
            vo.setIsAiGeneratedDictItemName(itemName);
        }

        return vo;
    }
}
