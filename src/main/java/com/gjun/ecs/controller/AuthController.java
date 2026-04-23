package com.gjun.ecs.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.gjun.ecs.dto.request.ChangePswReq;
import com.gjun.ecs.dto.request.LoginReq;
import com.gjun.ecs.dto.request.RegisterReq;
import com.gjun.ecs.dto.request.UpdateUserReq;
import com.gjun.ecs.dto.response.LoginResp;
import com.gjun.ecs.dto.response.Outbound;
import com.gjun.ecs.exception.ApplicationException;
import com.gjun.ecs.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
@Tag(name = "Authentication", description = "使用者認證相關 API")
public class AuthController extends BaseController {

  @Autowired
  private AuthService authService;

  /**
   * 使用者註冊
   * 
   * @param req
   * @return
   * @throws ApplicationException
   */
  @PostMapping("/register")
  @Operation(summary = "使用者註冊", description = "建立新使用者帳號")
  public ResponseEntity<Outbound> register(
      @Valid @RequestBody RegisterReq req)
      throws ApplicationException {
    Outbound response = authService.register(req);
    return ResponseEntity.status(HttpStatus.OK).body(response);

  }

  /**
   * 發送信箱驗證碼
   * @param payload 包含 email 的 Map
   * @return
   * @throws ApplicationException  <-- 必須加上這個
   */
  @PostMapping("/send-email-code")
  @Operation(summary = "發送信箱驗證碼", description = "模擬發送6位數信箱驗證碼")
  public ResponseEntity<Outbound>sendEmailCode(@RequestBody java.util.Map<String, String> payload)
    throws ApplicationException {
    
    // 從 Payload 取得前端傳來的 email
    String email = payload.get("email");

    // 呼叫 AuthService 的 sendSmsCode 方法傳送驗證碼並回傳結果
    Outbound response = authService.sendEmailCode(email);
    return ResponseEntity.status(HttpStatus.OK).body(response);
    }
  
  /**
   * 使用者登入
   * 
   * @param request
   * @return
   * @throws ApplicationException
   */

  @Value("${jwt.normal-expiration}")
    private long jwtExpiration;
    
  @Value("${jwt.remember-me-expiration}")
    private long jwtRememberMeExpiration;

  @PostMapping("/login")
  @Operation(summary = "使用者登入", description = "成功後會自動寫入 HttpOnly Cookie")
  public ResponseEntity<Outbound> login(
      @Valid @RequestBody LoginReq request,
      HttpServletResponse response)
      throws ApplicationException {
      Outbound outbound = authService.login(request);

      // 取出 LoginResp 物件
      LoginResp loginData = (LoginResp) outbound.getResult();

      // 從物件中抽取出 token 字串，用來寫入 Cookie
      String token = loginData.getToken();

        // 建立 HttpOnly Cookie
        Cookie cookie = new Cookie("token", token);
        cookie.setHttpOnly(true);  // 防止 JS 讀取 (防 XSS)
        cookie.setSecure(false);   // 開發環境 http 設 false，生產環境 https 設 true
        cookie.setPath("/");       // 全站路徑可用

        // 根據是否勾選保持登入來設定 Cookie 的壽命
        long finalMs = loginData.isRememberMe() ? jwtRememberMeExpiration : jwtExpiration;
        int finalMaxAge = (int) (finalMs / 1000);

        cookie.setMaxAge(finalMaxAge);
        response.addCookie(cookie);

    return ResponseEntity.status(HttpStatus.OK).body(outbound);
  }

  /**
   * 取得使用者資料
   * 
   * @param Bearertoken
   * @return
   * @throws ApplicationException
   */
  @GetMapping("/finduser")
  public ResponseEntity<Outbound> getUser()  throws ApplicationException{
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    Outbound response = authService.findUserByUsername(username);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  /**
   * 更新使用者資料
   * 
   * @param request
   * @return
   * @throws Exception
   */
  @PutMapping("/profile")
  @Operation(summary = "更新使用者資料")
  public ResponseEntity<Outbound> updateUserProfile(
      @Valid @RequestBody UpdateUserReq request) throws Exception {
    Outbound response = authService.updateUserProfile(request.getUsername(),
        request);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  /**
   * 取得目前使用者資料
   * 
   * @return
   */
  @GetMapping("/user")
  @Operation(summary = "取得目前使用者資料")
  public ResponseEntity<Outbound> getCurrentUser() {
    Outbound response = authService.getCurrentUser();
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  /**
   * 修改密碼
   * 
   * @param request
   * @return
   * @throws Exception
   */
  @PostMapping("/change-password")
  @Operation(summary = "修改密碼")
  public ResponseEntity<Outbound> changePassword(
      @Valid @RequestBody ChangePswReq request) throws Exception {
    Outbound response = authService.updatePassword(request);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  /**
   * 登出使用者
   */
  @PostMapping("/logout")
  @Operation(summary = "使用者登出", description = "手動清除瀏覽器端的 Cookie")
  public ResponseEntity<Outbound> logout(HttpServletResponse response) {
    
      // 建立同名且立即過期的 Cookie
        Cookie cookie = new Cookie("token", null);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
    
        response.addCookie(cookie);

    Outbound resp = authService.logout();
    return ResponseEntity.status(HttpStatus.OK).body(resp);
  }
}
