package com.fishing.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class JwtUtils {

    // 密钥，实际项目中应该从配置文件读取
    private static final String SECRET_KEY = "fishing-club-secret-key-2025-very-secure-and-long-enough-key-for-jwt-hmac-sha-algorithms";
    
    // 过期时间：24小时
    private static final long EXPIRE_TIME = 24 * 60 * 60 * 1000;

    // 生成JWT令牌
    public static String generateToken(Long userId, String username) {
        // 创建JWT声明
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("timestamp", new Date());

        // 生成密钥
        Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

        // 构建JWT令牌
        String token = Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return token;
    }

    // 解析JWT令牌
    public static Claims parseToken(String token) {
        try {
            Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            log.error("JWT解析失败: {}", e.getMessage());
            return null;
        }
    }

    // 从令牌中获取用户ID
    public static Long getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        if (claims != null) {
            return claims.get("userId", Long.class);
        }
        return null;
    }

    // 从令牌中获取用户名
    public static String getUsernameFromToken(String token) {
        Claims claims = parseToken(token);
        if (claims != null) {
            return claims.get("username", String.class);
        }
        return null;
    }

    // 验证令牌是否有效
    public static boolean validateToken(String token) {
        try {
            Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            log.error("JWT验证失败: {}", e.getMessage());
            return false;
        }
    }
}
