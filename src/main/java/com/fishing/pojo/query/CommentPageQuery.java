package com.fishing.pojo.query;

import lombok.Data;

import java.io.Serializable;

@Data
public class CommentPageQuery implements Serializable {
    private Long pageNum = 1L;
    private Long pageSize = 10L;
    private Long postId;
    private Long userId;
    private String content;
    private String isAiGeneratedDictItemCode;
}
