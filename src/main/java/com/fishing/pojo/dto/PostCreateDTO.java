package com.fishing.pojo.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class PostCreateDTO {
    private String title;
    private String content;
    private String typeDictTypeCode;
    private String typeDictItemCode;
    private String fishSpeciesDictTypeCode;
    private String fishSpeciesDictItemCode;
    private BigDecimal fishWeight;
    private String address;
    private List<String> images;
    private String aiAuditStatusDictTypeCode;
    private String aiAuditStatusDictItemCode;
    private String statusDictTypeCode;
    private String statusDictItemCode;
}
