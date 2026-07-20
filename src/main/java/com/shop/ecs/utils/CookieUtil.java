package com.shop.ecs.utils;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CookieUtil {
    
    private final boolean secure = false;
    private static final String COOKIE_NAME = "token";

    // 設定 JWT Cookie
    public void setJwtCookie(HttpServletResponse response, String token, int maxAge) {
        ResponseCookie cookie = ResponseCookie.from(COOKIE_NAME, token)
                .httpOnly(true)       // 防禦 XSS 攻擊
                .secure(secure)       // 是否開啟 HTTPS 傳輸
                .sameSite("Lax")      // 防禦 CSRF 攻擊
                .path("/")            // 全站路徑可用
                .maxAge(maxAge)       // 存活時間
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        
    }

    // 清除 JWT Cookie     
    public void clearJwtCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from(COOKIE_NAME, null)
                .httpOnly(true)
                .secure(secure)
                .sameSite("Lax")      // 屬性必須一致，瀏覽器才會刪除
                .path("/")
                .maxAge(0)            
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    
    }
}
