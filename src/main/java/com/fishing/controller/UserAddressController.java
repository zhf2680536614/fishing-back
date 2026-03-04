package com.fishing.controller;

import com.fishing.pojo.Result;
import com.fishing.pojo.dto.UserAddressDTO;
import com.fishing.pojo.vo.UserAddressVO;
import com.fishing.service.UserAddressService;
import com.fishing.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/address")
public class UserAddressController {

    @Autowired
    private UserAddressService userAddressService;

    @GetMapping("/list")
    public Result<List<UserAddressVO>> getUserAddresses(HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        List<UserAddressVO> addresses = userAddressService.getUserAddresses(userId);
        return Result.success(addresses);
    }

    @PostMapping("/create")
    public Result<UserAddressVO> createAddress(@RequestBody UserAddressDTO dto, HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        UserAddressVO address = userAddressService.createAddress(dto, userId);
        return Result.success(address);
    }

    @PutMapping("/{id}")
    public Result<UserAddressVO> updateAddress(@PathVariable String id, @RequestBody UserAddressDTO dto, HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        UserAddressVO address = userAddressService.updateAddress(Long.parseLong(id), dto, userId);
        return Result.success(address);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteAddress(@PathVariable String id, HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        userAddressService.deleteAddress(Long.parseLong(id), userId);
        return Result.success();
    }

    @PutMapping("/{id}/default")
    public Result<Void> setDefaultAddress(@PathVariable String id, HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        userAddressService.setDefaultAddress(Long.parseLong(id), userId);
        return Result.success();
    }

    private Long getCurrentUserId(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            Long userId = JwtUtils.getUserIdFromToken(token);
            if (userId != null) {
                return userId;
            }
        }
        return 1L;
    }
}