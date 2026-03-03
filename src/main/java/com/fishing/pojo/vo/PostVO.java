package com.fishing.pojo.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PostVO {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    private Long userId;
    private String userNickname;
    private String userAvatar;
    private Integer type;
    private String title;
    private String content;
    private List<String> images;
    private String fishSpecies;
    private BigDecimal fishWeight;
    private String addressName;
    private String aiComment;
    private Integer viewCount;
    private Integer likeCount;
    private Integer commentCount;
    private LocalDateTime createTime;
}
