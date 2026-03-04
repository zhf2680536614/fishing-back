package com.fishing.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fishing.pojo.dto.UserAddressDTO;
import com.fishing.pojo.entity.UserAddressEntity;
import com.fishing.pojo.vo.UserAddressVO;

import java.util.List;

public interface UserAddressService extends IService<UserAddressEntity> {
    List<UserAddressVO> getUserAddresses(Long userId);
    UserAddressVO createAddress(UserAddressDTO dto, Long userId);
    UserAddressVO updateAddress(Long id, UserAddressDTO dto, Long userId);
    void deleteAddress(Long id, Long userId);
    void setDefaultAddress(Long id, Long userId);
}