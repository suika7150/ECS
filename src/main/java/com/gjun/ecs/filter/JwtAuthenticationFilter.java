package com.gjun.ecs.filter;

import java.io.IOException;
import jakarta.servlet.http.Cookie;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.gjun.ecs.entity.UserInfo;
import com.gjun.ecs.enums.ResultCode;
import com.gjun.ecs.service.UserService;
import com.gjun.ecs.utils.JwtUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private UserService userService;

	@Override
	public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

	// 放行預檢請求
    if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
        response.setStatus(HttpServletResponse.SC_OK);
        filterChain.doFilter(request, response); // 繼續執行後續 filter，保證 CORS header 正確回傳
        return;
    }

	// 從 Cookie 中拿 Token
	String token = null;
	if(request.getCookies() != null) {
		for(Cookie cookie : request.getCookies()) {
			if("token".equals(cookie.getName())){
				token = cookie.getValue();
				break;
			}
		}
	}

	// 如果拿到 Token 開始驗證邏輯
	if(token != null) {
		try{
			String username = jwtUtil.getUsernameFromToken(token);
			
			if(username != null && SecurityContextHolder.getContext().getAuthentication() == null){
				UserInfo userInfo = userService.findUserByUsername(username);
				
				if(jwtUtil.validateToken(token, userInfo)){
						// 建立認證物件
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                userInfo, null, null); // Role 可以塞進第三個參數
				
						authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        
                        // 存入 SecurityContext，AuthController 就能 getName() 拿到帳號
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                        
				
				}
			}
		}catch(Exception e){
			System.out.println("Cookie Token 解析失敗: " + e.getMessage());
			
			ResultCode status = ResultCode.UNAUTHORIZED; 
    		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    		response.setContentType("application/json;charset=UTF-8");
			response.setCharacterEncoding("UTF-8");
		
			// 瀏覽器刪除 token cookie
			Cookie cookie = new Cookie("token", null);
			cookie.setPath("/");
			cookie.setMaxAge(0); // 設為 0 代表立刻刪除
			response.addCookie(cookie);

			response.getWriter().write(String.format(
        "{\"code\": \"%s\", \"msg\": \"%s\"}", 
        		status.getCode(), 
        		status.getMsg()
    		));

			return;
			
			}	
		}
		
		filterChain.doFilter(request, response);

		
	}

}
