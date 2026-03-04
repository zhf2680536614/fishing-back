package com.fishing.controller;

import com.fishing.pojo.Result;
import com.fishing.pojo.dto.CreateOrderDTO;
import com.fishing.pojo.vo.OrderVO;
import com.fishing.service.OrderService;
import com.fishing.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
        OrderVO order = orderService.getById(orderId);
        return Result.success(order);
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
