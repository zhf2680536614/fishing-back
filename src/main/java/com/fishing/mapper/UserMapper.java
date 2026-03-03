package com.fishing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fishing.pojo.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<UserEntity> {
}
