package com.fishing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fishing.exception.BusinessException;
import com.fishing.mapper.DictItemMapper;
import com.fishing.mapper.PostMapper;
import com.fishing.mapper.UserBadgeMapper;
import com.fishing.mapper.UserMapper;
import com.fishing.pojo.PageResult;
import com.fishing.pojo.dto.UserLoginDTO;
import com.fishing.pojo.dto.UserManageUpdateDTO;
import com.fishing.pojo.dto.UserRegisterDTO;
import com.fishing.pojo.dto.UserUpdateDTO;
import com.fishing.pojo.entity.DictItemEntity;
import com.fishing.pojo.entity.UserEntity;
import com.fishing.pojo.query.UserPageQuery;
import com.fishing.pojo.vo.UserManageVO;
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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEntity> implements UserService {

    @Resource
    private PostMapper postMapper;

    @Resource
    private UserBadgeMapper userBadgeMapper;

    @Resource
    private BadgeService badgeService;

    @Resource
    private DictItemMapper dictItemMapper;

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

        if ("banned".equals(user.getStatusDictItemCode())) {
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
        user.setRoleDictTypeCode(registerDTO.getRoleDictTypeCode() != null ? registerDTO.getRoleDictTypeCode() : "user_role");
        user.setRoleDictItemCode(registerDTO.getRoleDictItemCode() != null ? registerDTO.getRoleDictItemCode() : "user");
        user.setIsMaster(0);
        user.setExpPoints(0);
        user.setStatusDictTypeCode(registerDTO.getStatusDictTypeCode() != null ? registerDTO.getStatusDictTypeCode() : "user_status");
        user.setStatusDictItemCode(registerDTO.getStatusDictItemCode() != null ? registerDTO.getStatusDictItemCode() : "normal");

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
        Integer fishingDays = postMapper.countFishingDays(userId);
        Integer fishDays = postMapper.countFishDays(userId);
        Integer airForceDays = postMapper.countAirForce(userId);
        Double totalWeight = postMapper.sumFishWeight(userId);

        badgeService.checkAndAwardBadges(
                userId,
                fishingDays != null ? fishingDays : 0,
                fishDays != null ? fishDays : 0,
                airForceDays != null ? airForceDays : 0,
                totalWeight != null ? totalWeight : 0.0
        );

        var obtainedBadges = badgeService.getUserBadges(userId);
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

    @Override
    public PageResult<UserManageVO> page(UserPageQuery query) {
        LambdaQueryWrapper<UserEntity> wrapper = new LambdaQueryWrapper<>();
        
        if (query.getUsername() != null && !query.getUsername().isEmpty()) {
            wrapper.like(UserEntity::getUsername, query.getUsername());
        }
        if (query.getNickname() != null && !query.getNickname().isEmpty()) {
            wrapper.like(UserEntity::getNickname, query.getNickname());
        }
        if (query.getPhone() != null && !query.getPhone().isEmpty()) {
            wrapper.like(UserEntity::getPhone, query.getPhone());
        }
        if (query.getRoleDictItemCode() != null && !query.getRoleDictItemCode().isEmpty()) {
            wrapper.eq(UserEntity::getRoleDictItemCode, query.getRoleDictItemCode());
        }
        if (query.getStatusDictItemCode() != null && !query.getStatusDictItemCode().isEmpty()) {
            wrapper.eq(UserEntity::getStatusDictItemCode, query.getStatusDictItemCode());
        }
        
        wrapper.orderByDesc(UserEntity::getCreateTime);
        
        Page<UserEntity> page = this.page(new Page<>(query.getPageNum(), query.getPageSize()), wrapper);
        
        List<UserManageVO> voList = page.getRecords().stream().map(this::convertToManageVO).collect(Collectors.toList());
        
        PageResult<UserManageVO> pageResult = new PageResult<>();
        pageResult.setList(voList);
        pageResult.setTotal(page.getTotal());
        pageResult.setPageNum(page.getCurrent());
        pageResult.setPageSize(page.getSize());
        
        return pageResult;
    }

    @Override
    public UserManageVO getUserManageById(Long id) {
        UserEntity user = this.getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return convertToManageVO(user);
    }

    @Override
    public void updateUserManage(Long id, UserManageUpdateDTO dto) {
        UserEntity user = this.getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        if (dto.getNickname() != null) {
            user.setNickname(dto.getNickname());
        }
        if (dto.getAvatar() != null) {
            user.setAvatar(dto.getAvatar());
        }
        if (dto.getPhone() != null) {
            user.setPhone(dto.getPhone());
        }
        if (dto.getSignature() != null) {
            user.setSignature(dto.getSignature());
        }
        if (dto.getRoleDictTypeCode() != null) {
            user.setRoleDictTypeCode(dto.getRoleDictTypeCode());
        }
        if (dto.getRoleDictItemCode() != null) {
            user.setRoleDictItemCode(dto.getRoleDictItemCode());
        }
        if (dto.getStatusDictTypeCode() != null) {
            user.setStatusDictTypeCode(dto.getStatusDictTypeCode());
        }
        if (dto.getStatusDictItemCode() != null) {
            user.setStatusDictItemCode(dto.getStatusDictItemCode());
        }
        
        this.updateById(user);
    }

    @Override
    public void deleteUser(Long id) {
        UserEntity user = this.getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        this.removeById(id);
    }

    private UserManageVO convertToManageVO(UserEntity entity) {
        UserManageVO vo = new UserManageVO();
        BeanUtils.copyProperties(entity, vo);
        
        List<DictItemEntity> roleItems = dictItemMapper.selectByDictCode("user_role");
        if (roleItems != null) {
            roleItems.stream()
                .filter(item -> item.getItemCode().equals(entity.getRoleDictItemCode()))
                .findFirst()
                .ifPresent(item -> vo.setRoleDictItemName(item.getItemName()));
        }
        
        List<DictItemEntity> statusItems = dictItemMapper.selectByDictCode("user_status");
        if (statusItems != null) {
            statusItems.stream()
                .filter(item -> item.getItemCode().equals(entity.getStatusDictItemCode()))
                .findFirst()
                .ifPresent(item -> vo.setStatusDictItemName(item.getItemName()));
        }
        
        return vo;
    }
}
