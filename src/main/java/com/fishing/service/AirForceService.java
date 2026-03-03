package com.fishing.service;

import com.fishing.pojo.dto.AirForceCheckinDTO;
import com.fishing.pojo.vo.AirForcePostVO;
import com.fishing.pojo.vo.AirForceStatsVO;

import java.util.List;

/**
 * 空军服务接口
 */
public interface AirForceService {
    
    /**
     * 空军打卡
     *
     * @param dto    打卡信息
     * @param userId 用户ID
     * @return 打卡结果
     */
    AirForcePostVO checkin(AirForceCheckinDTO dto, Long userId);
    
    /**
     * 获取空军帖子列表
     *
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @param sortType 排序类型：newest-最新，hottest-最热
     * @param userId   当前用户ID（用于判断是否点赞）
     * @return 帖子列表
     */
    List<AirForcePostVO> getPostList(Integer pageNum, Integer pageSize, String sortType, Long userId);
    
    /**
     * 获取空军统计数据
     *
     * @param userId 用户ID
     * @return 统计数据
     */
    AirForceStatsVO getStats(Long userId);
    
    /**
     * 点赞/取消点赞
     *
     * @param postId 帖子ID
     * @param userId 用户ID
     * @return 是否点赞
     */
    Boolean toggleLike(Long postId, Long userId);
    
    /**
     * 生成AI安慰语
     *
     * @param content 空军经历
     * @return AI安慰语
     */
    String generateAIComment(String content);
}
