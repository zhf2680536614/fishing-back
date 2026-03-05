package com.fishing.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.SneakyThrows;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("biz_post")
public class PostEntity implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String typeDictTypeCode;

    private String typeDictItemCode;

    private String title;

    private String content;

    private String images;

    private String fishSpeciesDictTypeCode;

    private String fishSpeciesDictItemCode;

    private BigDecimal fishWeight;

    private Long spotId;

    private String addressName;

    private Integer viewCount;

    private Integer likeCount;

    private Integer commentCount;

    private String aiAuditStatusDictTypeCode;

    private String aiAuditStatusDictItemCode;

    private String aiAuditReason;

    private String statusDictTypeCode;

    private String statusDictItemCode;

    private String aiComment;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer isDeleted;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 获取图片列表
     */
    @SneakyThrows
    public List<String> getImageList() {
        if (images == null || images.isEmpty()) {
            return null;
        }
        return objectMapper.readValue(images, new TypeReference<List<String>>() {});
    }

    /**
     * 设置图片列表
     */
    @SneakyThrows
    public void setImageList(List<String> imageList) {
        if (imageList == null || imageList.isEmpty()) {
            this.images = null;
        } else {
            this.images = objectMapper.writeValueAsString(imageList);
        }
    }
}
