package com.fishing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fishing.pojo.entity.PostEntity;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PostMapper extends BaseMapper<PostEntity> {

    @Select("SELECT p.*, u.nickname as user_nickname, u.avatar as user_avatar " +
            "FROM biz_post p " +
            "LEFT JOIN sys_user u ON p.user_id = u.id " +
            "WHERE p.type = #{type} AND p.is_deleted = 0 " +
            "ORDER BY p.create_time DESC")
    List<PostEntity> selectListWithUser(Integer type);
}

