package com.fishing.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fishing.pojo.PageResult;
import com.fishing.pojo.dto.PostCreateDTO;
import com.fishing.pojo.dto.PostUpdateDTO;
import com.fishing.pojo.entity.PostEntity;
import com.fishing.pojo.query.PostPageQuery;
import com.fishing.pojo.vo.PostManageVO;
import com.fishing.pojo.vo.PostVO;

import java.util.List;

public interface PostService extends IService<PostEntity> {

    /**
     * 创建帖子
     */
    PostVO createPost(PostCreateDTO dto, Long userId);

    /**
     * 获取帖子列表
     */
    List<PostVO> getPostList(String typeDictItemCode, Integer pageNum, Integer pageSize);

    /**
     * 获取帖子详情
     */
    PostVO getPostDetail(Long id);

    /**
     * 增加浏览量
     */
    void incrementViewCount(Long id);

    /**
     * 点赞/取消点赞
     */
    Boolean toggleLike(Long postId, Long userId);

    /**
     * 分页查询帖子列表（管理后台）
     */
    PageResult<PostManageVO> page(PostPageQuery query);

    /**
     * 根据ID获取帖子管理详情
     */
    PostManageVO getPostManageById(Long id);

    /**
     * 更新帖子
     */
    void updatePost(Long id, PostUpdateDTO dto);

    /**
     * 删除帖子
     */
    void deletePost(Long id);
}
