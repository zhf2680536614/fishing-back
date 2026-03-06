package com.fishing.pojo.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class FishManageVO implements Serializable {
    private Long id;
    private String name;
    private String alias;
    private String category;
    private Integer protectionLevel;
    private String habits;
    private String edibleValue;
    private List<String> images;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
