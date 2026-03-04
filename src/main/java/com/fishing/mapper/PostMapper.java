package com.fishing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fishing.pojo.entity.PostEntity;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface PostMapper extends BaseMapper<PostEntity> {

    @Select("SELECT p.*, u.nickname as user_nickname, u.avatar as user_avatar " +
            "FROM biz_post p " +
            "LEFT JOIN sys_user u ON p.user_id = u.id " +
            "WHERE p.type = #{type} AND p.is_deleted = 0 " +
            "ORDER BY p.create_time DESC")
    List<PostEntity> selectListWithUser(Integer type);

    @Select("SELECT COUNT(*) FROM biz_post WHERE user_id = #{userId} AND is_deleted = 0")
    Integer countFishingDays(Long userId);

    @Select("SELECT COUNT(*) FROM biz_post WHERE user_id = #{userId} AND type = 0 AND is_deleted = 0")
    Integer countFishDays(Long userId);

    @Select("SELECT COALESCE(SUM(fish_weight), 0) FROM biz_post WHERE user_id = #{userId} AND type = 0 AND is_deleted = 0")
    Double sumFishWeight(Long userId);

    @Select("SELECT COUNT(*) FROM biz_post WHERE user_id = #{userId} AND type = 1 AND is_deleted = 0")
    Integer countAirForce(Long userId);

    /**
     * 查询每月出钓天数
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 每月出钓天数列表
     */
    @Select("SELECT DATE_FORMAT(create_time, '%Y-%m') as month, " +
            "COUNT(DISTINCT DATE(create_time)) as days " +
            "FROM biz_post " +
            "WHERE user_id = #{userId} " +
            "AND is_deleted = 0 " +
            "AND DATE(create_time) BETWEEN #{startDate} AND #{endDate} " +
            "GROUP BY DATE_FORMAT(create_time, '%Y-%m') " +
            "ORDER BY month")
    List<Map<String, Object>> selectMonthlyFishingDays(@Param("userId") Long userId,
                                                       @Param("startDate") String startDate,
                                                       @Param("endDate") String endDate);

    /**
     * 查询每日鱼获重量
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 每日鱼获重量列表
     */
    @Select("SELECT DATE(create_time) as date, COALESCE(SUM(fish_weight), 0) as weight " +
            "FROM biz_post " +
            "WHERE user_id = #{userId} " +
            "AND is_deleted = 0 " +
            "AND type = 0 " +
            "AND DATE(create_time) BETWEEN #{startDate} AND #{endDate} " +
            "GROUP BY DATE(create_time) " +
            "ORDER BY date")
    List<Map<String, Object>> selectDailyFishWeight(@Param("userId") Long userId,
                                                    @Param("startDate") String startDate,
                                                    @Param("endDate") String endDate);

    /**
     * 查询每月空军统计数据
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 每月空军统计数据列表
     */
    @Select("SELECT DATE_FORMAT(create_time, '%Y-%m') as month, " +
            "COUNT(*) as total_trips, " +
            "SUM(CASE WHEN type = 1 THEN 1 ELSE 0 END) as air_force_count " +
            "FROM biz_post " +
            "WHERE user_id = #{userId} " +
            "AND is_deleted = 0 " +
            "AND DATE(create_time) BETWEEN #{startDate} AND #{endDate} " +
            "GROUP BY DATE_FORMAT(create_time, '%Y-%m') " +
            "ORDER BY month")
    List<Map<String, Object>> selectMonthlyAirForceStats(@Param("userId") Long userId,
                                                         @Param("startDate") String startDate,
                                                         @Param("endDate") String endDate);
}

