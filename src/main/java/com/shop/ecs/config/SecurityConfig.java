package com.shop.ecs.config;

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

import com.shop.ecs.filter.JwtAuthenticationFilter;

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
                                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                                .sessionManagement(
                                                session -> session.sessionCreationPolicy(
                                                                org.springframework.security.config.http.SessionCreationPolicy.STATELESS))
                                .authorizeHttpRequests(
                                                auth -> auth
                                                                // 白名單路徑
                                                                .requestMatchers(
                                                                                "/api/login",
                                                                                "/api/login/verify-email-code",
                                                                                "/api/logout",
                                                                                "/api/register",
                                                                                "/api/send-email-code",
                                                                                "/api/verify-email-code",
                                                                                "/api/products/**",
                                                                                "/api/payment/callback",
                                                                                "/swagger-ui/**",
                                                                                "/v3/api-docs/**",
                                                                                "/swagger-ui.html",
                                                                                "/api/payment/params/**")
                                                                .permitAll()
                                                                .anyRequest()
                                                                .authenticated())
                                .exceptionHandling(
                                                exception -> exception.authenticationEntryPoint(
                                                                (request, response, authException) -> {
                                                                        response.setStatus(jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED);  
                                                                        response.setContentType("application/json;charset=UTF-8");
                                                                        response.getWriter().write("{\"code\": \"401\", \"msg\": \"尚未登入或登入已逾時\"}");
                                                                }))
                                .logout(
                                                logout -> logout
                                                                .logoutUrl("/api/logout")// 監聽登出路徑
                                                                .deleteCookies("token")// 自動刪除 Cookie
                                                                .clearAuthentication(true)// 自動清除 SecurityContext 身分暫存
                                                                .invalidateHttpSession(true)// 自動讓 Session 失效
                                                                .logoutSuccessHandler((request, response, authentication) -> {
                                                                        com.fasterxml.jackson.databind.ObjectMapper objectMapper = 
                                                                                new com.fasterxml.jackson.databind.ObjectMapper();// Spring 內建的 JSON 轉換器
                                                                        com.shop.ecs.common.result.Outbound outbound = 
                                                                                com.shop.ecs.common.result.Outbound.ok("登出成功");        
                                                                        response.setContentType("application/json;charset=UTF-8");
                                                                        response.getWriter().write(objectMapper.writeValueAsString(outbound));
                                                                }))                                
                                .addFilterBefore(
                                                jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
                return http.build();
        }

        @Bean
        CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration config = new CorsConfiguration();
                // 跨域允許清單，指定區網前端 URL
                config.setAllowedOriginPatterns(
                                List.of(
                                                "http://localhost:5173", // 本地
                                                "http://192.168.1.152:5173", // 本地
                                                "https://palladous-upmost-margaretta.ngrok-free.dev" // ngrok
                                ));
                config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                config.setAllowedHeaders(List.of("*"));
                config.setAllowCredentials(true);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", config);
                return source;
        }
}
