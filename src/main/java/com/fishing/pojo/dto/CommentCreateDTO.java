package com.fishing.pojo.dto;

import lombok.Data;

@Data
public class CommentCreateDTO {
    private Long postId;
    private Long parentId;
    private String content;
}
