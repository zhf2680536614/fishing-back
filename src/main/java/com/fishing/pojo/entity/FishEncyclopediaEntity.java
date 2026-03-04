package com.fishing.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("base_fish_encyclopedia")
public class FishEncyclopediaEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String name;

    private String alias;

    private String category;

    @TableField("protection_level")
    private Integer protectionLevel;

    private String habits;

    @TableField("edible_value")
    private String edibleValue;

    @TableField("img_url")
    private String imgUrl;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    @TableField("is_deleted")
    private Integer isDeleted;
}
