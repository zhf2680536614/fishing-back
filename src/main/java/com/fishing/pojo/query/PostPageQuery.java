package com.fishing.pojo.query;

import lombok.Data;

import java.io.Serializable;

@Data
public class PostPageQuery implements Serializable {
    private Long pageNum = 1L;
    private Long pageSize = 10L;
    private String title;
    private String typeDictItemCode;
    private String statusDictItemCode;
    private String aiAuditStatusDictItemCode;
    private Long userId;
}
