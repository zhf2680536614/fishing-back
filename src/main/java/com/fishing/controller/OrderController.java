package com.fishing.controller;

import com.fishing.pojo.Result;
import com.fishing.pojo.dto.CreateOrderDTO;
import com.fishing.pojo.dto.OrderUpdateStatusDTO;
import com.fishing.pojo.query.OrderPageQuery;
import com.fishing.pojo.vo.OrderManageVO;
import com.fishing.pojo.vo.OrderVO;
import com.fishing.pojo.PageResult;
import com.fishing.service.OrderService;
import com.fishing.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
@Slf4j
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/create")
    public Result<OrderVO> createOrder(@RequestBody CreateOrderDTO dto, HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        OrderVO order = orderService.createOrder(dto, userId);
        return Result.success(order);
    }

    @GetMapping("/{id}")
    public Result<OrderVO> getById(@PathVariable String id) {
        Long orderId = Long.parseLong(id);
        OrderVO order = orderService.getOrderById(orderId);
        return Result.success(order);
    }

    @GetMapping("/user/{userId}")
    public Result<List<OrderVO>> getUserOrders(@PathVariable Long userId, HttpServletRequest request) {
        Long currentUserId = getCurrentUserId(request);
        if (!currentUserId.equals(userId)) {
            return Result.error("无权访问");
        }
        List<OrderVO> orders = orderService.getUserOrders(userId);
        return Result.success(orders);
    }

    @PostMapping("/{orderId}/confirm-payment")
    public Result<Void> confirmPayment(@PathVariable Long orderId, HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        orderService.confirmPayment(orderId, userId);
        return Result.success();
    }

    @DeleteMapping("/{orderId}")
    public Result<Void> deleteOrder(@PathVariable Long orderId, HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        orderService.deleteOrder(orderId, userId);
        return Result.success();
    }

    @DeleteMapping("/batch")
    public Result<Void> batchDeleteOrders(@RequestBody List<Long> orderIds, HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        orderService.batchDeleteOrders(orderIds, userId);
        return Result.success();
    }

    // ==================== 管理后台接口 ====================

    /**
     * 分页查询订单列表（管理后台）
     */
    @PostMapping("/manage/page")
    public Result<PageResult<OrderManageVO>> managePage(@RequestBody OrderPageQuery query) {
        log.info("分页查询订单列表（管理后台）：{}", query);
        PageResult<OrderManageVO> pageResult = orderService.page(query);
        return Result.success(pageResult);
    }

    /**
     * 根据ID获取订单管理详情
     */
    @GetMapping("/manage/{id}")
    public Result<OrderManageVO> getOrderManageById(@PathVariable Long id) {
        log.info("获取订单管理详情：{}", id);
        OrderManageVO vo = orderService.getOrderManageById(id);
        if (vo == null) {
            return Result.error("订单不存在");
        }
        return Result.success(vo);
    }

    /**
     * 更新订单状态（管理后台）
     */
    @PutMapping("/manage/{id}/status")
    public Result<Void> updateOrderStatus(@PathVariable Long id, @RequestBody OrderUpdateStatusDTO dto) {
        log.info("更新订单状态（管理后台）：{}，状态：{}", id, dto.getStatusDictItemCode());
        orderService.updateOrderStatus(id, dto);
        return Result.success();
    }

    /**
     * 删除订单（管理后台）
     */
    @DeleteMapping("/manage/{id}")
    public Result<Void> deleteOrderManage(@PathVariable Long id) {
        log.info("删除订单（管理后台）：{}", id);
        orderService.deleteOrderManage(id);
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
