package com.fishing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fishing.exception.BusinessException;
import com.fishing.mapper.UserMapper;
import com.fishing.pojo.dto.UserLoginDTO;
import com.fishing.pojo.dto.UserRegisterDTO;
import com.fishing.pojo.entity.UserEntity;
import com.fishing.pojo.vo.UserVO;
import com.fishing.service.UserService;
import com.fishing.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEntity> implements UserService {

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
}
