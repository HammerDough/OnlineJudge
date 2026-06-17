package com.hammerdough.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expire}")
    private long ttlMillis;

    // 生成合规密钥
    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 生成JWT
     * @param claims 自定义载荷
     * @return token
     */
    public String createJWT(Map<String, Object> claims) {
        long expMillis = System.currentTimeMillis() + ttlMillis;
        Date exp = new Date(expMillis);

        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(exp)
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 解析token，获取载荷
     */
    public Claims parseJWT(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 简易校验token是否有效（不过期、不篡改）
     */
    public boolean validateToken(String token) {
        try {
            parseJWT(token);
            return true;
        } catch (ExpiredJwtException e) {
            // 令牌过期
            return false;
        } catch (JwtException e) {
            // 令牌非法、篡改
            return false;
        }
    }
}