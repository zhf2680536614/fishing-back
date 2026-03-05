package com.fishing.pojo.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PostManageVO implements Serializable {
    private Long id;
    private Long userId;
    private String userNickname;
    private String userAvatar;
    private String typeDictTypeCode;
    private String typeDictItemCode;
    private String typeDictItemName;
    private String title;
    private String content;
    private List<String> images;
    private String fishSpeciesDictTypeCode;
    private String fishSpeciesDictItemCode;
    private String fishSpeciesDictItemName;
    private BigDecimal fishWeight;
    private Long spotId;
    private String spotName;
    private String addressName;
    private Integer viewCount;
    private Integer likeCount;
    private Integer commentCount;
    private String aiAuditStatusDictTypeCode;
    private String aiAuditStatusDictItemCode;
    private String aiAuditStatusDictItemName;
    private String aiAuditReason;
    private String statusDictTypeCode;
    private String statusDictItemCode;
    private String statusDictItemName;
    private String aiComment;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
