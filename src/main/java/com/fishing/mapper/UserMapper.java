package com.fishing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fishing.pojo.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;

@Mapper
public interface UserMapper extends BaseMapper<UserEntity> {
    @Select("SELECT COUNT(*) FROM sys_user WHERE is_deleted = 0")
    Long countTotalUsers();

    @Select("SELECT COUNT(*) FROM sys_user WHERE is_deleted = 0 AND DATE(create_time) = CURDATE()")
    Long countTodayNewUsers();

    @Select("SELECT id, username, nickname, avatar, DATE_FORMAT(create_time, '%Y-%m-%d %H:%i') as createTime " +
            "FROM sys_user WHERE is_deleted = 0 ORDER BY create_time DESC LIMIT 5")
    java.util.List<com.fishing.pojo.vo.DashboardStatsVO.LatestUser> getLatestUsers();

    @Select("SELECT DATE(create_time) as date, COUNT(*) as value " +
            "FROM sys_user WHERE is_deleted = 0 AND create_time >= #{startDate} " +
            "GROUP BY DATE(create_time) ORDER BY date")
    java.util.List<com.fishing.pojo.vo.DashboardStatsVO.TrendData> getUserTrend(LocalDateTime startDate);
}
