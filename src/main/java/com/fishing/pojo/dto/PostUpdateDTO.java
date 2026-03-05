package com.fishing.pojo.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class PostUpdateDTO implements Serializable {
    private String typeDictTypeCode;
    private String typeDictItemCode;
    private String title;
    private String content;
    private List<String> images;
    private String fishSpeciesDictTypeCode;
    private String fishSpeciesDictItemCode;
    private BigDecimal fishWeight;
    private Long spotId;
    private String addressName;
    private String aiAuditStatusDictTypeCode;
    private String aiAuditStatusDictItemCode;
    private String aiAuditReason;
    private String statusDictTypeCode;
    private String statusDictItemCode;
    private String aiComment;
}
