package com.fishing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fishing.exception.BusinessException;
import com.fishing.mapper.PostMapper;
import com.fishing.mapper.UserBadgeMapper;
import com.fishing.mapper.UserMapper;
import com.fishing.pojo.dto.UserLoginDTO;
import com.fishing.pojo.dto.UserRegisterDTO;
import com.fishing.pojo.dto.UserUpdateDTO;
import com.fishing.pojo.entity.UserEntity;
import com.fishing.pojo.vo.UserProfileVO;
import com.fishing.pojo.vo.UserVO;
import com.fishing.service.BadgeService;
import com.fishing.service.UserService;
import com.fishing.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import jakarta.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEntity> implements UserService {

    @Resource
    private PostMapper postMapper;

    @Resource
    private UserBadgeMapper userBadgeMapper;

    @Resource
    private BadgeService badgeService;

    @Override
    public UserVO login(UserLoginDTO loginDTO) {
        String username = loginDTO.getUsername();
        String password = loginDTO.getPassword();

        LambdaQueryWrapper<UserEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserEntity::getUsername, username);
        UserEntity user = this.getOne(wrapper);

        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        String encryptedPassword = DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8));
        if (!encryptedPassword.equals(user.getPassword())) {
            throw new BusinessException("密码错误");
        }

        if (user.getStatus() == 0) {
            throw new BusinessException("账号已被封禁");
        }

        String token = JwtUtils.generateToken(user.getId(), user.getUsername());

        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        userVO.setToken(token);

        return userVO;
    }

    @Override
    public UserVO register(UserRegisterDTO registerDTO) {
        String username = registerDTO.getUsername();
        String password = registerDTO.getPassword();
        String nickname = registerDTO.getNickname();
        String phone = registerDTO.getPhone();

        LambdaQueryWrapper<UserEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserEntity::getUsername, username);
        if (this.count(wrapper) > 0) {
            throw new BusinessException("用户名已存在");
        }

        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setPassword(DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8)));
        user.setNickname(nickname != null ? nickname : username);
        user.setPhone(phone);
        user.setRole(0);
        user.setIsMaster(0);
        user.setExpPoints(0);
        user.setStatus(1);

        this.save(user);

        String token = JwtUtils.generateToken(user.getId(), user.getUsername());

        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        userVO.setToken(token);

        return userVO;
    }

    @Override
    public UserVO getUserInfo(Long userId) {
        UserEntity user = this.getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    @Override
    public UserProfileVO getProfile(Long userId) {
        UserEntity user = this.getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        UserProfileVO profileVO = new UserProfileVO();
        BeanUtils.copyProperties(user, profileVO);

        int expPoints = user.getExpPoints();
        int level = Math.min(expPoints / 1000 + 1, 10);
        profileVO.setLevel(level);

        Integer fishingDays = postMapper.countFishingDays(userId);
        profileVO.setFishingDays(fishingDays != null ? fishingDays : 0);

        Double totalFishWeight = postMapper.sumFishWeight(userId);
        profileVO.setTotalFishWeight(totalFishWeight != null ? totalFishWeight : 0.0);

        Integer airForceCount = postMapper.countAirForce(userId);
        profileVO.setAirForceCount(airForceCount != null ? airForceCount : 0);

        Integer badgeCount = userBadgeMapper.countBadges(userId);
        profileVO.setBadgeCount(badgeCount != null ? badgeCount : 0);

        return profileVO;
    }

    @Override
    public UserVO updateProfile(Long userId, UserUpdateDTO updateDTO) {
        UserEntity user = this.getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        if (updateDTO.getNickname() != null) {
            user.setNickname(updateDTO.getNickname());
        }
        if (updateDTO.getAvatar() != null) {
            user.setAvatar(updateDTO.getAvatar());
        }
        if (updateDTO.getPhone() != null) {
            user.setPhone(updateDTO.getPhone());
        }
        if (updateDTO.getSignature() != null) {
            user.setSignature(updateDTO.getSignature());
        }

        this.updateById(user);

        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    @Override
    public Map<String, Object> getUserBadges(Long userId) {
        // 获取用户钓鱼数据
        Integer fishingDays = postMapper.countFishingDays(userId);
        Integer fishDays = postMapper.countFishDays(userId);
        Integer airForceDays = postMapper.countAirForce(userId);
        Double totalWeight = postMapper.sumFishWeight(userId);

        // 检查并颁发勋章
        badgeService.checkAndAwardBadges(
                userId,
                fishingDays != null ? fishingDays : 0,
                fishDays != null ? fishDays : 0,
                airForceDays != null ? airForceDays : 0,
                totalWeight != null ? totalWeight : 0.0
        );

        // 获取已获得的勋章
        var obtainedBadges = badgeService.getUserBadges(userId);
        // 获取未获得的勋章
        var unobtainedBadges = badgeService.getUnobtainedBadges(
                userId,
                fishingDays != null ? fishingDays : 0,
                fishDays != null ? fishDays : 0,
                airForceDays != null ? airForceDays : 0,
                totalWeight != null ? totalWeight : 0.0
        );

        Map<String, Object> result = new HashMap<>();
        result.put("obtained", obtainedBadges);
        result.put("unobtained", unobtainedBadges);
        return result;
    }
}
