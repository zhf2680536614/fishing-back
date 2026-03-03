package com.fishing.service;

import com.fishing.pojo.dto.UserLoginDTO;
import com.fishing.pojo.dto.UserRegisterDTO;
import com.fishing.pojo.vo.UserVO;

public interface UserService {
    UserVO login(UserLoginDTO loginDTO);

    UserVO register(UserRegisterDTO registerDTO);

    UserVO getUserInfo(Long userId);
}
