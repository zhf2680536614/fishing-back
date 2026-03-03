package com.fishing.pojo.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 热门帖子VO（今日爆护榜）
 */
@Data
public class HotPostVO implements Serializable {

    /**
     * 帖子ID
     */
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 鱼种
     */
    private String fishSpecies;

    /**
     * 鱼重量
     */
    private BigDecimal fishWeight;

    /**
     * 封面图片
     */
    private String coverImage;

    /**
     * 用户昵称
     */
    private String userNickname;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
