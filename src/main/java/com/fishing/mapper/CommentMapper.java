package com.fishing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fishing.pojo.entity.CommentEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommentMapper extends BaseMapper<CommentEntity> {
}
