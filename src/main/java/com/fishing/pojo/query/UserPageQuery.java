package com.fishing.pojo.query;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserPageQuery implements Serializable {
    private Long pageNum = 1L;
    private Long pageSize = 10L;
    private String username;
    private String nickname;
    private String phone;
    private String roleDictItemCode;
    private String statusDictItemCode;
}
