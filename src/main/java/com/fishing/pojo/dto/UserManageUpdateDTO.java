package com.fishing.pojo.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserManageUpdateDTO implements Serializable {
    private String nickname;
    private String avatar;
    private String phone;
    private String signature;
    private String roleDictTypeCode;
    private String roleDictItemCode;
    private String statusDictTypeCode;
    private String statusDictItemCode;
}
