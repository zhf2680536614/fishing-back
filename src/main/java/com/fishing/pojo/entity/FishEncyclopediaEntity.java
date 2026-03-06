package com.fishing.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("base_fish_encyclopedia")
public class FishEncyclopediaEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String alias;
    private String category;
    private Integer protectionLevel;
    private String habits;
    private String edibleValue;
    private String images;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer isDeleted;
}
