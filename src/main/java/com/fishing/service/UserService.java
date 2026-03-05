package com.fishing.service;

import com.fishing.pojo.PageResult;
import com.fishing.pojo.dto.UserLoginDTO;
import com.fishing.pojo.dto.UserManageUpdateDTO;
import com.fishing.pojo.dto.UserRegisterDTO;
import com.fishing.pojo.dto.UserUpdateDTO;
import com.fishing.pojo.query.UserPageQuery;
import com.fishing.pojo.vo.UserManageVO;
import com.fishing.pojo.vo.UserProfileVO;
import com.fishing.pojo.vo.UserVO;

import java.util.Map;

public interface UserService {
    UserVO login(UserLoginDTO loginDTO);

    UserVO register(UserRegisterDTO registerDTO);

    UserVO getUserInfo(Long userId);

    UserProfileVO getProfile(Long userId);

    UserVO updateProfile(Long userId, UserUpdateDTO updateDTO);

    Map<String, Object> getUserBadges(Long userId);

    PageResult<UserManageVO> page(UserPageQuery query);

    UserManageVO getUserManageById(Long id);

    void updateUserManage(Long id, UserManageUpdateDTO dto);

    void deleteUser(Long id);
}
