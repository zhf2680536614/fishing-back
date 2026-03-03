package com.fishing.service;

import com.fishing.pojo.vo.HomeStatsVO;
import com.fishing.pojo.vo.HotPostVO;

import java.util.List;

/**
 * 首页服务接口
 */
public interface HomeService {

    /**
     * 获取首页统计数据
     */
    HomeStatsVO getHomeStats();

    /**
     * 获取今日爆护榜（当天钓鱼重量最多的前三名）
     */
    List<HotPostVO> getTodayHotPosts();
}
