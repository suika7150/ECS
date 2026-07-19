package com.shop.ecs.utils;

import com.shop.ecs.config.JwtProperties;
import com.shop.ecs.entity.UserEntity;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

  @Autowired private JwtProperties jwtProperties;

  public String generateToken(UserEntity userInfo, boolean rememberMe) {

    long expiration =
        rememberMe ? jwtProperties.getRememberMeExpiration() : jwtProperties.getNormalExpiration();

    Date expirationDate = new Date(System.currentTimeMillis() + expiration);

    return Jwts.builder()
        .setSubject(userInfo.getUsername()) // 設定主要身份資訊
        .claim("role", userInfo.getRole()) // 加入自定義資訊
        .claim("rm", rememberMe) // 加入保持登入的資訊
        .setIssuedAt(new Date()) // 簽發時間
        .setExpiration(expirationDate) // 過期時間
        .signWith(getSignKey(), SignatureAlgorithm.HS256) // HMAC SHA256 簽章
        .compact(); // 建立 JWT 字串
  }

  public String getUsernameFromToken(String token) {
    return extractAllClaims(token).getSubject();
  }

  public boolean validateToken(String token, UserEntity userInfo) {
    try {
      final String username = getUsernameFromToken(token);
      return username.equals(userInfo.getUsername()) && !isTokenExpired(token);
    } catch (Exception e) {
    	
      return false;
    }
  }


  private boolean isTokenExpired(String token) {

    Date exp = extractAllClaims(token).getExpiration();
    boolean expired = exp.before(new Date());
    return expired;
  }

  private Claims extractAllClaims(String token) {
	  
    return Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(token).getBody();
  }

  private Key getSignKey() {
  
    return Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
  }
}
