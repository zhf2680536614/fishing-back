package com.fishing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fishing.pojo.entity.PostLikeEntity;
import org.apache.ibatis.annotations.*;

@Mapper
public interface PostLikeMapper extends BaseMapper<PostLikeEntity> {
    @Select("SELECT * FROM biz_post_like WHERE post_id = #{postId} AND user_id = #{userId}")
    PostLikeEntity selectByPostAndUser(@Param("postId") Long postId, @Param("userId") Long userId);

    @Delete("DELETE FROM biz_post_like WHERE post_id = #{postId} AND user_id = #{userId}")
    void deleteByPostAndUser(@Param("postId") Long postId, @Param("userId") Long userId);
}
