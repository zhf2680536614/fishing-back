package com.fishing.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fishing.exception.BusinessException;
import com.fishing.mapper.UserAddressMapper;
import com.fishing.pojo.dto.UserAddressDTO;
import com.fishing.pojo.entity.UserAddressEntity;
import com.fishing.pojo.vo.UserAddressVO;
import com.fishing.service.UserAddressService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserAddressServiceImpl extends ServiceImpl<UserAddressMapper, UserAddressEntity> implements UserAddressService {

    @Override
    public List<UserAddressVO> getUserAddresses(Long userId) {
        List<UserAddressEntity> addresses = this.lambdaQuery()
                .eq(UserAddressEntity::getUserId, userId)
                .eq(UserAddressEntity::getIsDeleted, 0)
                .orderByDesc(UserAddressEntity::getIsDefault)
                .orderByDesc(UserAddressEntity::getCreateTime)
                .list();
        
        return addresses.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserAddressVO createAddress(UserAddressDTO dto, Long userId) {
        // 如果设置为默认地址，先将其他地址设为非默认
        if (dto.getIsDefault() != null && dto.getIsDefault() == 1) {
            this.lambdaUpdate()
                    .eq(UserAddressEntity::getUserId, userId)
                    .eq(UserAddressEntity::getIsDeleted, 0)
                    .set(UserAddressEntity::getIsDefault, 0)
                    .update();
        }
        
        UserAddressEntity address = new UserAddressEntity();
        BeanUtils.copyProperties(dto, address);
        address.setUserId(userId);
        address.setIsDeleted(0);
        
        if (dto.getIsDefault() == null) {
            address.setIsDefault(0);
        }
        
        this.save(address);
        return convertToVO(address);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserAddressVO updateAddress(Long id, UserAddressDTO dto, Long userId) {
        UserAddressEntity address = this.getById(id);
        if (address == null || address.getIsDeleted() == 1) {
            throw new BusinessException("地址不存在");
        }
        if (!address.getUserId().equals(userId)) {
            throw new BusinessException("无权操作此地址");
        }
        
        // 如果设置为默认地址，先将其他地址设为非默认
        if (dto.getIsDefault() != null && dto.getIsDefault() == 1) {
            this.lambdaUpdate()
                    .eq(UserAddressEntity::getUserId, userId)
                    .eq(UserAddressEntity::getIsDeleted, 0)
                    .set(UserAddressEntity::getIsDefault, 0)
                    .update();
        }
        
        BeanUtils.copyProperties(dto, address);
        this.updateById(address);
        return convertToVO(address);
    }

    @Override
    public void deleteAddress(Long id, Long userId) {
        UserAddressEntity address = this.getById(id);
        if (address == null || address.getIsDeleted() == 1) {
            throw new BusinessException("地址不存在");
        }
        if (!address.getUserId().equals(userId)) {
            throw new BusinessException("无权操作此地址");
        }
        
        address.setIsDeleted(1);
        this.updateById(address);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setDefaultAddress(Long id, Long userId) {
        UserAddressEntity address = this.getById(id);
        if (address == null || address.getIsDeleted() == 1) {
            throw new BusinessException("地址不存在");
        }
        if (!address.getUserId().equals(userId)) {
            throw new BusinessException("无权操作此地址");
        }
        
        // 将其他地址设为非默认
        this.lambdaUpdate()
                .eq(UserAddressEntity::getUserId, userId)
                .eq(UserAddressEntity::getIsDeleted, 0)
                .set(UserAddressEntity::getIsDefault, 0)
                .update();
        
        // 将当前地址设为默认
        address.setIsDefault(1);
        this.updateById(address);
    }

    private UserAddressVO convertToVO(UserAddressEntity address) {
        UserAddressVO vo = new UserAddressVO();
        BeanUtils.copyProperties(address, vo);
        return vo;
    }
}