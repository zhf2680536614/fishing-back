package com.fishing.controller;

import com.fishing.pojo.Result;
import com.fishing.pojo.dto.CreateOrderDTO;
import com.fishing.pojo.vo.OrderVO;
import com.fishing.service.OrderService;
import com.fishing.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
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
