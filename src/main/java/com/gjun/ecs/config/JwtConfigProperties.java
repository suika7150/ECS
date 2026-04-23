package com.gjun.ecs.config;

import org.springframework.stereotype.Component;
import lombok.Data;

@Data
@Component
public class JwtConfigProperties {
    private String secret;
    private Long normalExpiration;
    private Long rememberMeExpiration;
}
