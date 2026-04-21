package com.gjun.ecs.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "jwt") // 自動找 jwt.secret
public class JwtProperties {
    private String secret;
    private long normalExpiration;     // 一般登入 (毫秒)
    private long rememberMeExpiration; // 保持登入 (毫秒)

}