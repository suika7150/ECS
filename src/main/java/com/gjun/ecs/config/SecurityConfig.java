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
        .cors(cors -> cors.configurationSource(corsConfigurationSource())) // 開啟CORS
        
        // 設定為無狀態模式，不產生 HttpSession
        .sessionManagement(session -> session
            .sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.STATELESS)
        )

        .authorizeHttpRequests(auth -> auth

          // 白名單路徑
              .requestMatchers( 
                "/api/login",
                "/api/logout", 
                "/api/register", 
                "/api/products/**",
                "/swagger-ui/**", 
                "/v3/api-docs/**", 
                "/swagger-ui.html",
                "/api/payment/params/**"
              ).permitAll() // 允許所有 API 都可訪問 

            // 確保綠界 Callback 接口是完全公開的，且不經過 JWT 驗證
            // .requestMatchers("/api/payment/params/**").permitAll()
            
            // 其他靜態資源或測試路徑
            .anyRequest().authenticated())
            .exceptionHandling(exception -> exception
            .authenticationEntryPoint((request, response, authException) -> {
                response.setStatus(jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED); // 強制回傳 401
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"code\": \"401\", \"msg\": \"尚未登入或登入已逾時\"}");
            })
        )
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
        "http://172.20.10.13:5173", // 個人熱點測試電腦
        "https://palladous-upmost-margaretta.ngrok-free.dev" // ngrok 服務器(我的測試服務器)
        
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

/*
未來若要細分權限，可以在 authorizeHttpRequests 裡面這樣寫：
.authorizeHttpRequests(auth -> auth
    // 1. 公開資源：登入、註冊、發送簡訊、Swagger 文件
    .requestMatchers("/api/login", "/api/register", "/api/send-sms", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
    // 2. 特殊權限：例如只有管理員能刪除商品
    .requestMatchers("/api/admin/**").hasRole("ADMIN")
    // 3. 其餘所有 API 都必須通過 JWT 驗證
    .anyRequest().authenticated() 
)
*/