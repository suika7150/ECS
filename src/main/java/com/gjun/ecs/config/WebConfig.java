//設置都在SecurityConfig.java設置

// package com.gjun.ecs.config;

// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.web.servlet.config.annotation.CorsRegistry;
// import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// @Configuration
// public class WebConfig {

//     @Bean
//     public WebMvcConfigurer corsConfigurer() {
//         return new WebMvcConfigurer(){
//             @Override
//             public void addCorsMappings(CorsRegistry registry) {
//                 registry.addMapping("/**") // 所有 API
//                         .allowedOrigins("http://192.168.50.43:5173") // 前端 URL
//                         .allowedMethods("GET","POST","PUT","DELETE","OPTIONS") // 允許所有方法
//                         .allowedHeaders("*") // 所有 header
//                         .allowCredentials(true); // 允許帶 cookie/session
//             }
//         };
//     }
// }