package com.fishing.config;

import com.fishing.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从请求头中获取Authorization字段
        String authorization = request.getHeader("Authorization");
        
        // 检查Authorization是否存在且格式正确
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"code\": 401, \"message\": \"未授权，请先登录\", \"data\": null, \"timestamp\": " + System.currentTimeMillis() + "}");
            return false;
        }
        
        // 提取token
        String token = authorization.substring(7);
        
        // 验证token
        if (!JwtUtils.validateToken(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"code\": 401, \"message\": \"token无效或已过期\", \"data\": null, \"timestamp\": " + System.currentTimeMillis() + "}");
            return false;
        }
        
        // 从token中获取用户信息并存储到请求上下文
        Long userId = JwtUtils.getUserIdFromToken(token);
        String username = JwtUtils.getUsernameFromToken(token);
        
        if (userId != null && username != null) {
            request.setAttribute("userId", userId);
            request.setAttribute("username", username);
        }
        
        return true;
    }
}
