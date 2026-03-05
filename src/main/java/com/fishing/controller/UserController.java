package com.fishing.controller;

import com.fishing.pojo.PageResult;
import com.fishing.pojo.dto.UserLoginDTO;
import com.fishing.pojo.dto.UserManageUpdateDTO;
import com.fishing.pojo.dto.UserRegisterDTO;
import com.fishing.pojo.dto.UserUpdateDTO;
import com.fishing.pojo.Result;
import com.fishing.pojo.query.UserPageQuery;
import com.fishing.pojo.vo.UserManageVO;
import com.fishing.pojo.vo.UserProfileVO;
import com.fishing.pojo.vo.UserVO;
import com.fishing.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "*")
@Slf4j
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public Result<UserVO> login(@RequestBody UserLoginDTO loginDTO) {
        log.info("用户登录：{}", loginDTO.getUsername());
        UserVO userVO = userService.login(loginDTO);
        return Result.success(userVO);
    }

    @PostMapping("/register")
    public Result<UserVO> register(@RequestBody UserRegisterDTO registerDTO) {
        log.info("用户注册：{}", registerDTO.getUsername());
        UserVO userVO = userService.register(registerDTO);
        return Result.success(userVO);
    }

    @GetMapping("/{id}")
    public Result<UserVO> getUserInfo(@PathVariable Long id) {
        log.info("获取用户信息：{}", id);
        UserVO userVO = userService.getUserInfo(id);
        return Result.success(userVO);
    }

    @GetMapping("/profile/{id}")
    public Result<UserProfileVO> getProfile(@PathVariable Long id) {
        log.info("获取用户个人中心数据：{}", id);
        UserProfileVO profileVO = userService.getProfile(id);
        return Result.success(profileVO);
    }

    @PutMapping("/profile/{id}")
    public Result<UserVO> updateProfile(@PathVariable Long id, @RequestBody UserUpdateDTO updateDTO) {
        log.info("更新用户资料：{}", id);
        UserVO userVO = userService.updateProfile(id, updateDTO);
        return Result.success(userVO);
    }

    @GetMapping("/badges/{id}")
    public Result<?> getUserBadges(@PathVariable Long id) {
        log.info("获取用户勋章：{}", id);
        return Result.success(userService.getUserBadges(id));
    }

    @PostMapping("/page")
    public Result<PageResult<UserManageVO>> page(@RequestBody UserPageQuery query) {
        log.info("分页查询用户列表：{}", query);
        PageResult<UserManageVO> pageResult = userService.page(query);
        return Result.success(pageResult);
    }

    @GetMapping("/manage/{id}")
    public Result<UserManageVO> getUserManageById(@PathVariable Long id) {
        log.info("获取用户管理详情：{}", id);
        UserManageVO userManageVO = userService.getUserManageById(id);
        return Result.success(userManageVO);
    }

    @PutMapping("/manage/{id}")
    public Result<Void> updateUserManage(@PathVariable Long id, @RequestBody UserManageUpdateDTO dto) {
        log.info("更新用户管理信息：{}", id);
        userService.updateUserManage(id, dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(@PathVariable Long id) {
        log.info("删除用户：{}", id);
        userService.deleteUser(id);
        return Result.success();
    }
}
