package com.fishing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fishing.pojo.entity.OrderEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;

@Mapper
public interface OrderMapper extends BaseMapper<OrderEntity> {
    @Select("SELECT COUNT(*) FROM biz_order WHERE is_deleted = 0")
    Long countTotalOrders();

    @Select("SELECT o.id, o.gear_title, o.total_amount, di.item_name as status, DATE_FORMAT(o.create_time, '%Y-%m-%d %H:%i') as createTime " +
            "FROM biz_order o " +
            "LEFT JOIN sys_dict_item di ON o.status_dict_item_code = di.item_code " +
            "WHERE o.is_deleted = 0 ORDER BY o.create_time DESC LIMIT 5")
    java.util.List<com.fishing.pojo.vo.DashboardStatsVO.LatestOrder> getLatestOrders();

    @Select("SELECT DATE(create_time) as date, COUNT(*) as value " +
            "FROM biz_order WHERE is_deleted = 0 AND create_time >= #{startDate} " +
            "GROUP BY DATE(create_time) ORDER BY date")
    java.util.List<com.fishing.pojo.vo.DashboardStatsVO.TrendData> getOrderTrend(LocalDateTime startDate);
}
