package com.fishing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fishing.pojo.entity.PostEntity;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;

@Mapper
public interface PostMapper extends BaseMapper<PostEntity> {

    @Select("SELECT p.*, u.nickname as user_nickname, u.avatar as user_avatar " +
            "FROM biz_post p " +
            "LEFT JOIN sys_user u ON p.user_id = u.id " +
            "WHERE p.type_dict_item_code = #{typeDictItemCode} AND p.is_deleted = 0 " +
            "ORDER BY p.create_time DESC")
    List<PostEntity> selectListWithUser(String typeDictItemCode);

    @Select("SELECT COUNT(*) FROM biz_post WHERE user_id = #{userId} AND is_deleted = 0")
    Integer countFishingDays(Long userId);

    @Select("SELECT COUNT(*) FROM biz_post WHERE user_id = #{userId} AND type_dict_item_code = 'catch_report' AND is_deleted = 0")
    Integer countFishDays(Long userId);

    @Select("SELECT COALESCE(SUM(fish_weight), 0) FROM biz_post WHERE user_id = #{userId} AND type_dict_item_code = 'catch_report' AND is_deleted = 0")
    Double sumFishWeight(Long userId);

    @Select("SELECT COUNT(*) FROM biz_post WHERE user_id = #{userId} AND type_dict_item_code = 'air_force' AND is_deleted = 0")
    Integer countAirForce(Long userId);

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

    @Select("SELECT DATE_FORMAT(create_time, '%Y-%m-%d') as date, COALESCE(SUM(fish_weight), 0) as weight " +
            "FROM biz_post " +
            "WHERE user_id = #{userId} " +
            "AND is_deleted = 0 " +
            "AND type_dict_item_code = 'catch_report' " +
            "AND DATE(create_time) BETWEEN #{startDate} AND #{endDate} " +
            "GROUP BY DATE_FORMAT(create_time, '%Y-%m-%d') " +
            "ORDER BY date")
    List<Map<String, Object>> selectDailyFishWeight(@Param("userId") Long userId,
                                                    @Param("startDate") String startDate,
                                                    @Param("endDate") String endDate);

    @Select("SELECT DATE_FORMAT(create_time, '%Y-%m') as month, " +
            "COUNT(*) as total_trips, " +
            "SUM(CASE WHEN type_dict_item_code = 'air_force' THEN 1 ELSE 0 END) as air_force_count " +
            "FROM biz_post " +
            "WHERE user_id = #{userId} " +
            "AND is_deleted = 0 " +
            "AND DATE(create_time) BETWEEN #{startDate} AND #{endDate} " +
            "GROUP BY DATE_FORMAT(create_time, '%Y-%m') " +
            "ORDER BY month")
    List<Map<String, Object>> selectMonthlyAirForceStats(@Param("userId") Long userId,
                                                         @Param("startDate") String startDate,
                                                         @Param("endDate") String endDate);

    @Select("SELECT COUNT(*) FROM biz_post WHERE is_deleted = 0")
    Long countTotalPosts();

    @Select("SELECT COUNT(*) FROM biz_post WHERE is_deleted = 0 AND DATE(create_time) = CURDATE()")
    Long countTodayNewPosts();

    @Select("SELECT COUNT(*) FROM biz_post WHERE is_deleted = 0 AND status_dict_item_code = 'pending'")
    Long countPendingPosts();

    @Select("SELECT p.id, p.title, u.username, u.avatar, DATE_FORMAT(p.create_time, '%Y-%m-%d %H:%i') as createTime " +
            "FROM biz_post p " +
            "LEFT JOIN sys_user u ON p.user_id = u.id " +
            "WHERE p.is_deleted = 0 ORDER BY p.create_time DESC LIMIT 5")
    java.util.List<com.fishing.pojo.vo.DashboardStatsVO.LatestPost> getLatestPosts();

    @Select("SELECT DATE(create_time) as date, COUNT(*) as value " +
            "FROM biz_post WHERE is_deleted = 0 AND create_time >= #{startDate} " +
            "GROUP BY DATE(create_time) ORDER BY date")
    java.util.List<com.fishing.pojo.vo.DashboardStatsVO.TrendData> getPostTrend(LocalDateTime startDate);

    @Select("SELECT di.item_name as typeName, COUNT(*) as count " +
            "FROM biz_post p " +
            "LEFT JOIN sys_dict_item di ON p.type_dict_item_code = di.item_code " +
            "WHERE p.is_deleted = 0 GROUP BY p.type_dict_item_code, di.item_name")
    java.util.List<com.fishing.pojo.vo.DashboardStatsVO.PostTypeDistribution> getPostTypeDistribution();
}
