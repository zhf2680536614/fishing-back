package com.fishing.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("sys_dict_item")
public class DictItemEntity implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long dictTypeId;
    private String itemCode;
    private String itemName;
    private String value;
    private String description;
    private Integer sortOrder;
    private Integer status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer isDeleted;
}
