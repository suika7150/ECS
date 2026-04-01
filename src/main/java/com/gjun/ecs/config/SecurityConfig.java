package com.gjun.ecs.config;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.gjun.ecs.filter.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Autowired
  private JwtAuthenticationFilter jwtAuthenticationFilter;

  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf(csrf -> csrf.disable())
        .cors(cors -> cors.configurationSource(corsConfigurationSource())) // 開啟
                                                                           // CORS
                                                                           // 支援

        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/**", "/api/login", "/api/register", "/api/user",
                "/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html") // 白名單
            .permitAll() // 允許所有
            .anyRequest().permitAll())
        .addFilterBefore(jwtAuthenticationFilter,
            UsernamePasswordAuthenticationFilter.class);// 設定JWT驗證過濾器
    return http.build();
  }

  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    // 跨域允許清單，指定區網前端 URL 
    config.setAllowedOriginPatterns(List.of(
        "http://localhost:5173", // 本地開發
        "http://192.168.1.152:5173", // 家中電腦
        "http://192.168.50.43:5173", // 公司電腦
        "http://192.168.100.46:5173", // 公司電腦(德)
        "http://192.168.50.44:5173",  // 區網其他測試電腦
        "http://172.20.10.13:5173" // 個人熱點測試電腦
        
    ));
    config
        .setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    config.setAllowedHeaders(List.of("*"));
    config.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
  }
}
