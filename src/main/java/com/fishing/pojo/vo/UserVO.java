package com.fishing.pojo.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserVO implements Serializable {
    private Long id;
    private String username;
    private String nickname;
    private String avatar;
    private String phone;
    private String signature;
    private Integer role;
    private Integer isMaster;
    private Integer expPoints;
    private String token;
}
