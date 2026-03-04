package com.fishing.pojo.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;

@Data
public class UserProfileVO implements Serializable {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    private String username;
    private String nickname;
    private String avatar;
    private String phone;
    private String signature;
    private Integer role;
    private Integer isMaster;
    private Integer expPoints;
    private Integer level;
    private Integer fishingDays;
    private Double totalFishWeight;
    private Integer airForceCount;
    private Integer badgeCount;
}
