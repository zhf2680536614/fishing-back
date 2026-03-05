package com.fishing.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fishing.pojo.dto.CreateOrderDTO;
import com.fishing.pojo.dto.OrderUpdateStatusDTO;
import com.fishing.pojo.entity.OrderEntity;
import com.fishing.pojo.query.OrderPageQuery;
import com.fishing.pojo.vo.OrderManageVO;
import com.fishing.pojo.vo.OrderVO;
import com.fishing.pojo.PageResult;

import java.util.List;

public interface OrderService extends IService<OrderEntity> {
    OrderVO createOrder(CreateOrderDTO dto, Long userId);
    OrderVO getOrderById(Long id);
    List<OrderVO> getUserOrders(Long userId);
    void confirmPayment(Long orderId, Long userId);
    void deleteOrder(Long orderId, Long userId);
    void batchDeleteOrders(List<Long> orderIds, Long userId);

    PageResult<OrderManageVO> page(OrderPageQuery query);
    OrderManageVO getOrderManageById(Long id);
    void updateOrderStatus(Long id, OrderUpdateStatusDTO dto);
    void deleteOrderManage(Long id);
}
