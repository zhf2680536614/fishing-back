package com.fishing.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fishing.pojo.dto.CreateOrderDTO;
import com.fishing.pojo.entity.OrderEntity;
import com.fishing.pojo.vo.OrderVO;

public interface OrderService extends IService<OrderEntity> {
    OrderVO createOrder(CreateOrderDTO dto, Long userId);
    OrderVO getById(Long id);
}
