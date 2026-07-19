package com.shop.ecs.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
  private String secret;
  private long normalExpiration; // 一般登入
  private long rememberMeExpiration; // 保持登入
}
