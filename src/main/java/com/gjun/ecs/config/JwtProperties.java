package com.gjun.ecs.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "jwt") // 自動找 jwt.secret
public class JwtProperties {
    // 變數名稱必須跟 properties 裡的 . 之後對齊
    // jwt.secret -> secret
    private String secret;
}