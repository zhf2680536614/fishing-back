package com.fishing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fishing.mapper.FishingSpotMapper;
import com.fishing.mapper.PostLikeMapper;
import com.fishing.mapper.PostMapper;
import com.fishing.mapper.UserMapper;
import com.fishing.pojo.PageResult;
import com.fishing.pojo.dto.PostCreateDTO;
import com.fishing.pojo.dto.PostUpdateDTO;
import com.fishing.pojo.entity.FishingSpotEntity;
import com.fishing.pojo.entity.PostEntity;
import com.fishing.pojo.entity.PostLikeEntity;
import com.fishing.pojo.entity.UserEntity;
import com.fishing.pojo.query.PostPageQuery;
import com.fishing.pojo.vo.PostManageVO;
import com.fishing.pojo.vo.PostVO;
import com.fishing.service.DictService;
import com.fishing.service.PostService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

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
    private final FishingSpotMapper fishingSpotMapper;
    private final DictService dictService;
    private final ObjectMapper objectMapper;

    public PostServiceImpl(PostMapper postMapper, UserMapper userMapper, PostLikeMapper postLikeMapper,
                           FishingSpotMapper fishingSpotMapper, DictService dictService, ObjectMapper objectMapper) {
        this.postMapper = postMapper;
        this.userMapper = userMapper;
        this.postLikeMapper = postLikeMapper;
        this.fishingSpotMapper = fishingSpotMapper;
        this.dictService = dictService;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public PostVO createPost(PostCreateDTO dto, Long userId) {
        log.info("创建帖子，用户ID：{}，标题：{}", userId, dto.getTitle());

        PostEntity entity = new PostEntity();
        entity.setUserId(userId);
        entity.setTypeDictTypeCode(dto.getTypeDictTypeCode());
        entity.setTypeDictItemCode(dto.getTypeDictItemCode());
        entity.setTitle(dto.getTitle());
        entity.setContent(dto.getContent());
        entity.setFishSpeciesDictTypeCode(dto.getFishSpeciesDictTypeCode());
        entity.setFishSpeciesDictItemCode(dto.getFishSpeciesDictItemCode());
        entity.setFishWeight(dto.getFishWeight() != null ? dto.getFishWeight() : BigDecimal.ZERO);
        entity.setAddressName(dto.getAddress());
        entity.setViewCount(0);
        entity.setLikeCount(0);
        entity.setCommentCount(0);
        entity.setAiAuditStatusDictTypeCode(dto.getAiAuditStatusDictTypeCode());
        entity.setAiAuditStatusDictItemCode(dto.getAiAuditStatusDictItemCode());
        entity.setStatusDictTypeCode(dto.getStatusDictTypeCode());
        entity.setStatusDictItemCode(dto.getStatusDictItemCode());
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

        // 如果是鱼获战报且有鱼货重量，增加经验值（1kg = 1经验值，向上取整）
        if (dto.getFishWeight() != null && dto.getFishWeight().compareTo(BigDecimal.ZERO) > 0) {
            int expToAdd = (int) Math.ceil(dto.getFishWeight().doubleValue());
            UserEntity user = userMapper.selectById(userId);
            if (user != null) {
                int currentExp = user.getExpPoints() != null ? user.getExpPoints() : 0;
                user.setExpPoints(currentExp + expToAdd);
                userMapper.updateById(user);
                log.info("用户{}发布鱼获战报，鱼货重量{}kg，增加经验值{}，当前经验值{}", 
                    userId, dto.getFishWeight(), expToAdd, user.getExpPoints());
            }
        }

        return getPostDetail(entity.getId());
    }

    @Override
    public List<PostVO> getPostList(String typeDictItemCode, Integer pageNum, Integer pageSize) {
        // 使用 MyBatis-Plus 分页
        Page<PostEntity> page = new Page<>(pageNum, pageSize);
        
        LambdaQueryWrapper<PostEntity> queryWrapper = new LambdaQueryWrapper<>();
        
        // 根据类型参数设置查询条件
        if (typeDictItemCode != null && !typeDictItemCode.isEmpty()) {
            queryWrapper.eq(PostEntity::getTypeDictItemCode, typeDictItemCode);
        }
        
        queryWrapper.eq(PostEntity::getIsDeleted, 0)
                .orderByDesc(PostEntity::getCreateTime);
        
        IPage<PostEntity> resultPage = postMapper.selectPage(page, queryWrapper);

        return resultPage.getRecords().stream().map(this::convertToVO).collect(Collectors.toList());
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

    // ==================== 管理后台方法 ====================

    @Override
    public PageResult<PostManageVO> page(PostPageQuery query) {
        Page<PostEntity> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<PostEntity> wrapper = new LambdaQueryWrapper<>();

        // 标题模糊查询
        if (StringUtils.hasText(query.getTitle())) {
            wrapper.like(PostEntity::getTitle, query.getTitle());
        }

        // 类型筛选
        if (StringUtils.hasText(query.getTypeDictItemCode())) {
            wrapper.eq(PostEntity::getTypeDictItemCode, query.getTypeDictItemCode());
        }

        // 状态筛选
        if (StringUtils.hasText(query.getStatusDictItemCode())) {
            wrapper.eq(PostEntity::getStatusDictItemCode, query.getStatusDictItemCode());
        }

        // AI审核状态筛选
        if (StringUtils.hasText(query.getAiAuditStatusDictItemCode())) {
            wrapper.eq(PostEntity::getAiAuditStatusDictItemCode, query.getAiAuditStatusDictItemCode());
        }

        // 用户ID筛选
        if (query.getUserId() != null) {
            wrapper.eq(PostEntity::getUserId, query.getUserId());
        }

        wrapper.eq(PostEntity::getIsDeleted, 0)
                .orderByDesc(PostEntity::getCreateTime);

        IPage<PostEntity> entityPage = this.page(page, wrapper);
        List<PostManageVO> voList = entityPage.getRecords().stream()
                .map(this::convertToManageVO)
                .collect(Collectors.toList());

        PageResult<PostManageVO> result = new PageResult<>();
        result.setList(voList);
        result.setTotal(entityPage.getTotal());
        result.setPageNum(entityPage.getCurrent());
        result.setPageSize(entityPage.getSize());
        return result;
    }

    @Override
    public PostManageVO getPostManageById(Long id) {
        PostEntity entity = this.getById(id);
        if (entity == null || entity.getIsDeleted() == 1) {
            return null;
        }
        return convertToManageVO(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePost(Long id, PostUpdateDTO dto) {
        PostEntity entity = this.getById(id);
        if (entity == null || entity.getIsDeleted() == 1) {
            throw new RuntimeException("帖子不存在");
        }

        // 更新字段
        if (StringUtils.hasText(dto.getTypeDictTypeCode())) {
            entity.setTypeDictTypeCode(dto.getTypeDictTypeCode());
        }
        if (StringUtils.hasText(dto.getTypeDictItemCode())) {
            entity.setTypeDictItemCode(dto.getTypeDictItemCode());
        }
        if (StringUtils.hasText(dto.getTitle())) {
            entity.setTitle(dto.getTitle());
        }
        if (StringUtils.hasText(dto.getContent())) {
            entity.setContent(dto.getContent());
        }
        if (dto.getImages() != null) {
            try {
                entity.setImages(objectMapper.writeValueAsString(dto.getImages()));
            } catch (JsonProcessingException e) {
                log.error("图片列表转换JSON失败", e);
            }
        }
        if (StringUtils.hasText(dto.getFishSpeciesDictTypeCode())) {
            entity.setFishSpeciesDictTypeCode(dto.getFishSpeciesDictTypeCode());
        }
        if (StringUtils.hasText(dto.getFishSpeciesDictItemCode())) {
            entity.setFishSpeciesDictItemCode(dto.getFishSpeciesDictItemCode());
        }
        if (dto.getFishWeight() != null) {
            entity.setFishWeight(dto.getFishWeight());
        }
        if (dto.getSpotId() != null) {
            entity.setSpotId(dto.getSpotId());
        }
        if (StringUtils.hasText(dto.getAddressName())) {
            entity.setAddressName(dto.getAddressName());
        }
        if (StringUtils.hasText(dto.getAiAuditStatusDictTypeCode())) {
            entity.setAiAuditStatusDictTypeCode(dto.getAiAuditStatusDictTypeCode());
        }
        if (StringUtils.hasText(dto.getAiAuditStatusDictItemCode())) {
            entity.setAiAuditStatusDictItemCode(dto.getAiAuditStatusDictItemCode());
        }
        if (StringUtils.hasText(dto.getAiAuditReason())) {
            entity.setAiAuditReason(dto.getAiAuditReason());
        }
        if (StringUtils.hasText(dto.getStatusDictTypeCode())) {
            entity.setStatusDictTypeCode(dto.getStatusDictTypeCode());
        }
        if (StringUtils.hasText(dto.getStatusDictItemCode())) {
            entity.setStatusDictItemCode(dto.getStatusDictItemCode());
        }
        if (StringUtils.hasText(dto.getAiComment())) {
            entity.setAiComment(dto.getAiComment());
        }

        this.updateById(entity);
        log.info("更新帖子成功，ID：{}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePost(Long id) {
        PostEntity entity = this.getById(id);
        if (entity == null || entity.getIsDeleted() == 1) {
            throw new RuntimeException("帖子不存在");
        }

        entity.setIsDeleted(1);
        this.updateById(entity);
        log.info("删除帖子成功，ID：{}", id);
    }

    private PostManageVO convertToManageVO(PostEntity entity) {
        PostManageVO vo = new PostManageVO();
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

        // 获取钓点信息
        if (entity.getSpotId() != null) {
            FishingSpotEntity spot = fishingSpotMapper.selectById(entity.getSpotId());
            if (spot != null) {
                vo.setSpotName(spot.getName());
            }
        }

        // 获取字典项名称
        if (entity.getTypeDictTypeCode() != null && entity.getTypeDictItemCode() != null) {
            vo.setTypeDictItemName(dictService.getItemName(entity.getTypeDictTypeCode(), entity.getTypeDictItemCode()));
        }
        if (entity.getFishSpeciesDictTypeCode() != null && entity.getFishSpeciesDictItemCode() != null) {
            vo.setFishSpeciesDictItemName(dictService.getItemName(entity.getFishSpeciesDictTypeCode(), entity.getFishSpeciesDictItemCode()));
        }
        if (entity.getAiAuditStatusDictTypeCode() != null && entity.getAiAuditStatusDictItemCode() != null) {
            vo.setAiAuditStatusDictItemName(dictService.getItemName(entity.getAiAuditStatusDictTypeCode(), entity.getAiAuditStatusDictItemCode()));
        }
        if (entity.getStatusDictTypeCode() != null && entity.getStatusDictItemCode() != null) {
            vo.setStatusDictItemName(dictService.getItemName(entity.getStatusDictTypeCode(), entity.getStatusDictItemCode()));
        }

        return vo;
    }
}
