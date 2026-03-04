package com.fishing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fishing.pojo.entity.UserBadgeEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserBadgeMapper extends BaseMapper<UserBadgeEntity> {

    @Select("SELECT COUNT(*) FROM sys_user_badge WHERE user_id = #{userId} AND is_deleted = 0")
    Integer countBadges(Long userId);
}
