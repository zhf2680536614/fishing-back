package com.fishing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fishing.pojo.entity.UserAddressEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserAddressMapper extends BaseMapper<UserAddressEntity> {
}