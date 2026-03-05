package com.fishing.pojo.query;

import lombok.Data;

@Data
public class FishEncyclopediaPageQuery {
    private Long pageNum = 1L;
    private Long pageSize = 10L;
    private String name;
    private String alias;
    private String category;
    private Integer protectionLevel;
}
