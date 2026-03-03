package com.fishing.pojo.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class AirForcePostVO {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;
    private String userNickname;
    private String userAvatar;
    private String content;
    private List<String> images;
    private String aiComment;
    private Integer likeCount;
    private Integer commentCount;
    private Integer viewCount;
    private Boolean isLiked;
    private LocalDateTime createTime;
}
