package com.fishing.service.impl;

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

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
    public OrderVO getById(Long id) {
        OrderEntity order = super.getById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        return convertToVO(order);
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
        return vo;
    }
}
