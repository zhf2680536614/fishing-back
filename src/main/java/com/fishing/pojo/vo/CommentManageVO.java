package com.fishing.pojo.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class CommentManageVO implements Serializable {
    private Long id;
    private Long postId;
    private String postTitle;
    private Long userId;
    private String username;
    private String nickname;
    private String avatar;
    private Long parentId;
    private String content;
    private String isAiGeneratedDictTypeCode;
    private String isAiGeneratedDictItemCode;
    private String isAiGeneratedDictItemName;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
