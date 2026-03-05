package com.fishing.pojo.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class UserManageVO implements Serializable {
    private Long id;
    private String username;
    private String nickname;
    private String avatar;
    private String phone;
    private String signature;
    private String roleDictTypeCode;
    private String roleDictItemCode;
    private String roleDictItemName;
    private Integer isMaster;
    private Integer expPoints;
    private String statusDictTypeCode;
    private String statusDictItemCode;
    private String statusDictItemName;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
