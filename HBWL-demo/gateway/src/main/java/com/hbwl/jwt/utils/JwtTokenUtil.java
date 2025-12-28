package com.hbwl.jwt.utils;

import com.hbwl.jwt.properties.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class JwtTokenUtil {

    @Autowired
    private JwtProperties jwtProperties;

    // 生成JWT令牌
    public String generateToken(String userId, Map<String, String> payload) {
        int payloadSize = payload == null ? 0 : payload.size();
        Map<String, Object> claims = new HashMap<>(payloadSize + 1);
        claims.put("userId", userId);

        if (payloadSize > 0) {
            claims.putAll(payload);
        }

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtProperties.getExpiration()))
                .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecretKey())
                .compact();
    }

    public String getUserIdFromToken(String token) {
        String userId;
        try {
            Claims claims = getClaimsFromToken(token);
            userId = (String) claims.get("userId");
        } catch (Exception e) {
            log.error("从token中加载用户ID时出错:{}", e.getMessage());
            userId = null;
        }
        return userId;
    }

    public String getUsernameFromToken(String token) {
        String username;
        try {
            Claims claims = getClaimsFromToken(token);
            username = (String) claims.get("username");
        } catch (Exception e){
            log.error("从token中加载用户名时出错:{}", e.getMessage());
            username = null;
        }
        return username;
    }

    public String getUserRoleFromToken(String token) {
        String userRole;
        try {
            Claims claims = getClaimsFromToken(token);
            userRole = (String) claims.get("role");
        } catch (Exception e){
            log.error("从token中加载用户权限时出错:{}", e.getMessage());
            userRole = null;
        }
        return userRole;
    }

    public Boolean isTokenExpired(String token){
        try {
            Claims claims = getClaimsFromToken(token);
            Date expiration = claims.getExpiration();
            return expiration.before(new Date());
        } catch (Exception e){
            return true;
        }
    }

    public String refreshToken(String token){
        String refreshToken;
        try{
            Claims claims = getClaimsFromToken(token);
            claims.setIssuedAt(new Date());
            refreshToken = Jwts.builder()
                    .setClaims(claims)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + jwtProperties.getExpiration()))
                    .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecretKey())
                    .compact();
        } catch (Exception e){
            refreshToken = null;
        }
        return refreshToken;
    }

    public Boolean validateToken(String token, String userId){
        String id = getUserIdFromToken(token);
        return userId.equals(id) && !isTokenExpired(token);
    }

    private Claims getClaimsFromToken(String token) throws Exception {
        Claims claims;
        try {
            claims = Jwts.parser().setSigningKey(jwtProperties.getSecretKey()).parseClaimsJws(token).getBody();
        } catch (Exception e){
            //System.out.println("解析token出错！");//过期同样会报错
            log.error("从token中获取claim时出错:{}", e.getMessage());
            claims = null;
            throw new Exception(e);
        }
        return claims;
    }
}
