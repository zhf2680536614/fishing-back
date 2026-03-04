package com.fishing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fishing.exception.BusinessException;
import com.fishing.mapper.GearMarketMapper;
import com.fishing.mapper.OrderMapper;
import com.fishing.pojo.dto.CreateOrderDTO;
import com.fishing.pojo.entity.GearMarketEntity;
import com.fishing.pojo.entity.OrderEntity;
import com.fishing.pojo.vo.OrderVO;
import com.fishing.service.OrderService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, OrderEntity> implements OrderService {

    @Autowired
    private GearMarketMapper gearMarketMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderVO createOrder(CreateOrderDTO dto, Long userId) {
        // 1. 查询装备信息
        GearMarketEntity gear = gearMarketMapper.selectById(dto.getGearId());
        if (gear == null) {
            throw new BusinessException("装备不存在");
        }
        if (gear.getStatus() != 0) {
            throw new BusinessException("装备已下架或已售出");
        }
        // 检查是否是自己的商品
        if (gear.getUserId().equals(userId)) {
            throw new BusinessException("不能购买自己发布的商品");
        }

        // 2. 总金额直接使用装备价格
        BigDecimal totalAmount = gear.getPrice();

        // 3. 创建订单
        OrderEntity order = new OrderEntity();
        order.setUserId(userId);
        order.setGearId(gear.getId());
        order.setGearTitle(gear.getTitle());
        order.setGearPrice(gear.getPrice());
        order.setTotalAmount(totalAmount);
        order.setStatus(0); // 待付款
        order.setAddress(dto.getAddress());
        order.setContactPhone(dto.getContactPhone());
        order.setIsDeleted(0);

        // 4. 保存订单
        this.save(order);

        // 5. 更新装备状态为已售出
        gear.setStatus(1);
        gearMarketMapper.updateById(gear);

        // 6. 转换为VO返回
        return convertToVO(order);
    }

    @Override
    public OrderVO getOrderById(Long id) {
        OrderEntity order = super.getById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        return convertToVO(order);
    }

    @Override
    public List<OrderVO> getUserOrders(Long userId) {
        LambdaQueryWrapper<OrderEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderEntity::getUserId, userId)
               .eq(OrderEntity::getIsDeleted, 0)
               .orderByDesc(OrderEntity::getCreateTime);
        List<OrderEntity> orders = this.list(wrapper);
        return orders.stream().map(this::convertToVO).toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmPayment(Long orderId, Long userId) {
        OrderEntity order = super.getById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException("无权操作此订单");
        }
        if (order.getStatus() != 0) {
            throw new BusinessException("订单状态不正确");
        }
        
        order.setStatus(1);
        order.setUpdateTime(new Date());
        this.updateById(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteOrder(Long orderId, Long userId) {
        OrderEntity order = super.getById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException("无权操作此订单");
        }
        if (order.getStatus() == 2) {
            throw new BusinessException("已发货的订单不能删除");
        }
        
        this.removeById(orderId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteOrders(List<Long> orderIds, Long userId) {
        if (orderIds == null || orderIds.isEmpty()) {
            throw new BusinessException("请选择要删除的订单");
        }
        
        for (Long orderId : orderIds) {
            deleteOrder(orderId, userId);
        }
    }

    @Scheduled(fixedRate = 60000)
    @Transactional(rollbackFor = Exception.class)
    public void autoCompleteOrders() {
        Date thirtyMinutesAgo = new Date(System.currentTimeMillis() - 30 * 60 * 1000);
        LambdaQueryWrapper<OrderEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderEntity::getStatus, 1)
               .lt(OrderEntity::getUpdateTime, thirtyMinutesAgo);
        List<OrderEntity> orders = this.list(wrapper);
        
        for (OrderEntity order : orders) {
            order.setStatus(3);
            order.setUpdateTime(new Date());
            this.updateById(order);
        }
    }

    private OrderVO convertToVO(OrderEntity order) {
        OrderVO vo = new OrderVO();
        BeanUtils.copyProperties(order, vo);
        // 转换状态为文本
        switch (order.getStatus()) {
            case 0: vo.setStatusText("待付款"); break;
            case 1: vo.setStatusText("已付款"); break;
            case 2: vo.setStatusText("已发货"); break;
            case 3: vo.setStatusText("已完成"); break;
            case 4: vo.setStatusText("已取消"); break;
            default: vo.setStatusText("未知");
        }
        // 获取装备图片
        if (order.getGearId() != null) {
            GearMarketEntity gear = gearMarketMapper.selectById(order.getGearId());
            if (gear != null) {
                vo.setGearImages(gear.getImages());
            }
        }
        return vo;
    }
}
