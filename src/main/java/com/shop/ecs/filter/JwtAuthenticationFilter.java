package com.shop.ecs.filter;

import com.shop.ecs.constant.ResultCode;
import com.shop.ecs.entity.UserEntity;
import com.shop.ecs.service.UserService;
import com.shop.ecs.utils.CookieUtil;
import com.shop.ecs.utils.JwtUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  @Autowired
  private JwtUtil jwtUtil;

  @Autowired
  private CookieUtil cookieUtil;

  @Autowired
  private UserService userService;

  @Override
  public void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    // 放行預檢請求
    if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
      response.setStatus(HttpServletResponse.SC_OK);
      filterChain.doFilter(request, response); // 繼續執行後續 filter，保證 CORS header 正確回傳
      return;
    }

    // 從 Cookie 中拿 Token
    String token = null;
    if (request.getCookies() != null) {
      for (Cookie cookie : request.getCookies()) {
        if ("token".equals(cookie.getName())) {
          token = cookie.getValue();
          break;
        }
      }
    }

    // 如果拿到 Token 開始驗證邏輯
    if (token != null) {
      try {
        String username = jwtUtil.getUsernameFromToken(token);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
          UserEntity userInfo = userService.getUser(username);

          if (jwtUtil.validateToken(token, userInfo)) {
            //將 UserRoleEnum 轉成 Spring Security 認識的權限字串
            // Spring Security 預設的角色權限前綴通常是 "ROLE_"
            String authorityName = "ROLE_" + userInfo.getRole().name();
            SimpleGrantedAuthority authority = new SimpleGrantedAuthority(authorityName);
            // 建立認證物件
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                userInfo.getUsername(),
                null,
                Collections.singletonList(authority) // Role 可以塞進第三個參數
              );

            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // 存入 SecurityContext，AuthController 就能 getName() 拿到帳號
            SecurityContextHolder.getContext().setAuthentication(authToken);
          }
        }
      } catch (Exception e) {
        System.out.println("Cookie Token 解析失敗: " + e.getMessage());

        ResultCode status = ResultCode.UNAUTHORIZED;
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        cookieUtil.clearJwtCookie(response);

        response
            .getWriter()
            .write(
                String.format(
                    "{\"code\": \"%s\", \"msg\": \"%s\"}", status.getCode(), status.getMsg()));

        return;
      }
    }

    filterChain.doFilter(request, response);
  }
}
