package com.fishing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fishing.pojo.entity.CommentEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CommentMapper extends BaseMapper<CommentEntity> {
    @Select("SELECT COUNT(*) FROM biz_comment WHERE is_deleted = 0")
    Long countTotalComments();
}
