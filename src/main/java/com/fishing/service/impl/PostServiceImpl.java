package com.fishing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fishing.mapper.PostLikeMapper;
import com.fishing.mapper.PostMapper;
import com.fishing.mapper.UserMapper;
import com.fishing.pojo.dto.PostCreateDTO;
import com.fishing.pojo.entity.PostEntity;
import com.fishing.pojo.entity.PostLikeEntity;
import com.fishing.pojo.entity.UserEntity;
import com.fishing.pojo.vo.PostVO;
import com.fishing.service.PostService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PostServiceImpl extends ServiceImpl<PostMapper, PostEntity> implements PostService {

    private final PostMapper postMapper;
    private final UserMapper userMapper;
    private final PostLikeMapper postLikeMapper;
    private final ObjectMapper objectMapper;

    public PostServiceImpl(PostMapper postMapper, UserMapper userMapper, PostLikeMapper postLikeMapper, ObjectMapper objectMapper) {
        this.postMapper = postMapper;
        this.userMapper = userMapper;
        this.postLikeMapper = postLikeMapper;
        this.objectMapper = objectMapper;
    }

    @Override
    public PostVO createPost(PostCreateDTO dto, Long userId) {
        log.info("创建帖子，用户ID：{}，标题：{}", userId, dto.getTitle());

        PostEntity entity = new PostEntity();
        entity.setUserId(userId);
        entity.setType(0); // 0-鱼获战报
        entity.setTitle(dto.getTitle());
        entity.setContent(dto.getContent());
        entity.setFishSpecies(dto.getFishSpecies());
        entity.setFishWeight(dto.getFishWeight() != null ? dto.getFishWeight() : BigDecimal.ZERO);
        entity.setAddressName(dto.getAddress());
        entity.setViewCount(0);
        entity.setLikeCount(0);
        entity.setCommentCount(0);
        entity.setAiAuditStatus(1); // 1-正常
        entity.setIsDeleted(0); // 0-未删除

        // 处理图片列表，转为JSON字符串
        if (dto.getImages() != null && !dto.getImages().isEmpty()) {
            try {
                entity.setImages(objectMapper.writeValueAsString(dto.getImages()));
            } catch (JsonProcessingException e) {
                log.error("图片列表转换JSON失败", e);
                entity.setImages("[]");
            }
        } else {
            entity.setImages("[]");
        }

        postMapper.insert(entity);

        return getPostDetail(entity.getId());
    }

    @Override
    public List<PostVO> getPostList(Integer type) {
        List<PostEntity> list = postMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<PostEntity>()
                        .eq(PostEntity::getType, type)
                        .eq(PostEntity::getIsDeleted, 0)
                        .orderByDesc(PostEntity::getCreateTime)
        );

        return list.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public PostVO getPostDetail(Long id) {
        PostEntity entity = postMapper.selectById(id);
        if (entity == null || entity.getIsDeleted() == 1) {
            return null;
        }
        return convertToVO(entity);
    }

    @Override
    public void incrementViewCount(Long id) {
        PostEntity entity = postMapper.selectById(id);
        if (entity != null && entity.getIsDeleted() == 0) {
            entity.setViewCount(entity.getViewCount() + 1);
            postMapper.updateById(entity);
        }
    }

    @Override
    @Transactional
    public Boolean toggleLike(Long postId, Long userId) {
        PostEntity post = postMapper.selectById(postId);
        if (post == null || post.getIsDeleted() == 1) {
            return false;
        }

        PostLikeEntity existingLike = postLikeMapper.selectByPostAndUser(postId, userId);
        if (existingLike == null) {
            PostLikeEntity newLike = new PostLikeEntity();
            newLike.setPostId(postId);
            newLike.setUserId(userId);
            postLikeMapper.insert(newLike);
        }
        post.setLikeCount(post.getLikeCount() + 1);
        postMapper.updateById(post);
        return true;
    }

    private PostVO convertToVO(PostEntity entity) {
        PostVO vo = new PostVO();
        BeanUtils.copyProperties(entity, vo);

        // 解析图片JSON
        if (entity.getImages() != null && !entity.getImages().isEmpty()) {
            try {
                List<String> images = objectMapper.readValue(entity.getImages(),
                        objectMapper.getTypeFactory().constructCollectionType(List.class, String.class));
                vo.setImages(images);
            } catch (JsonProcessingException e) {
                log.error("解析图片JSON失败", e);
                vo.setImages(Arrays.asList());
            }
        } else {
            vo.setImages(Arrays.asList());
        }

        // 获取用户信息
        UserEntity user = userMapper.selectById(entity.getUserId());
        if (user != null) {
            vo.setUserNickname(user.getNickname());
            vo.setUserAvatar(user.getAvatar());
        }

        return vo;
    }
}
