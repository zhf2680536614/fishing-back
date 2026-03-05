package com.fishing.pojo.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;

@Data
public class UserVO implements Serializable {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    private String username;
    private String nickname;
    private String avatar;
    private String phone;
    private String signature;
    private String roleDictTypeCode;
    private String roleDictItemCode;
    private Integer isMaster;
    private Integer expPoints;
    private String statusDictTypeCode;
    private String statusDictItemCode;
    private String token;
}
