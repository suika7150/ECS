package com.shop.ecs.controller;

import com.shop.ecs.common.result.Outbound;
import com.shop.ecs.constant.ResultCode;
import com.shop.ecs.dto.request.ChangePswReq;
import com.shop.ecs.dto.request.LoginReq;
import com.shop.ecs.dto.request.RegisterReq;
import com.shop.ecs.dto.request.SendEmailCodeReq;
import com.shop.ecs.dto.request.UpdateUserReq;
import com.shop.ecs.dto.request.VerifyEmailCodeReq;
import com.shop.ecs.dto.request.VerifyLoginCodeReq;
import com.shop.ecs.dto.response.LoginResp;
import com.shop.ecs.exception.ApplicationException;
import com.shop.ecs.service.AuthService;
import com.shop.ecs.service.RecaptchaService;
import com.shop.ecs.utils.CookieUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import java.security.Principal;

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

@RestController
@RequestMapping("/api")
@Tag(name = "Authentication", description = "使用者認證相關 API")
public class AuthController {

  @Autowired
  private CookieUtil cookieUtil;

  @Autowired
  private AuthService authService;

  @Autowired
  private RecaptchaService recaptchaService;

  @Value("${jwt.normal-expiration}")
  private long jwtExpiration;

  @Value("${jwt.remember-me-expiration}")
  private long jwtRememberMeExpiration;

  @PostMapping("/register")
  @Operation(summary = "使用者註冊", description = "建立新使用者帳號")
  public ResponseEntity<Outbound> register(@Valid @RequestBody RegisterReq req)
      throws ApplicationException {
    return ResponseEntity.ok(authService.register(req));
  }

  @PostMapping("/send-email-code")
  @Operation(summary = "發送信箱驗證碼", description = "模擬發送6位數信箱驗證碼")
  public ResponseEntity<Outbound> sendEmailCode(@Valid@RequestBody SendEmailCodeReq req)
      throws ApplicationException {
    return ResponseEntity.ok(authService.sendEmailCode(req.getEmail(), req.getType()));
  }

  @PostMapping("/verify-email-code")
  @Operation(summary = "驗證信箱驗證碼", description = "驗證使用者輸入的信箱驗證碼")
  public ResponseEntity<Outbound> verifyEmailCode(
      @Valid @RequestBody VerifyEmailCodeReq req) throws ApplicationException {
    return ResponseEntity.ok(authService.verifyEmailCode(req.getEmail(), req.getCode(), req.getType()));
  }

  @PostMapping("/login")
  @Operation(summary = "使用者登入", description = "帳密成功後寄出 Email OTP")
  public ResponseEntity<Outbound> login(
      @Valid @RequestBody LoginReq request)
      throws ApplicationException {

    boolean isHuman = recaptchaService.verifyToken(request.getRecaptchaToken());
    if (!isHuman) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Outbound.error(ResultCode.VALIDATION_ERROR));
    }
    return ResponseEntity.ok(authService.login(request));
  }

  @PostMapping("/login/verify-email-code")
  @Operation(summary = "登入二階段驗證", description = "驗證 Email OTP，成功後寫入 HttpOnly Cookie")
  public ResponseEntity<Outbound> verifyLoginCode(
      @Valid @RequestBody VerifyLoginCodeReq request,
      HttpServletResponse response)
      throws ApplicationException {

    Outbound outbound = authService.verifyLoginCode(request);
    LoginResp loginData = (LoginResp) outbound.getResult();
    String token = loginData.getToken();

    // 根據是否勾選保持登入來設定 Cookie 的壽命
    long finalMs = loginData.isRememberMe() ? jwtRememberMeExpiration : jwtExpiration;
    int finalMaxAge = (int) (finalMs / 1000);

    cookieUtil.setJwtCookie(response, token, finalMaxAge);

    return ResponseEntity.ok(outbound);
  }

  @GetMapping("/user")
  public ResponseEntity<Outbound> getUser() throws ApplicationException {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    return ResponseEntity.ok(authService.getCurrentUser(username));
  }

  @PutMapping("/profile")
  @Operation(summary = "更新使用者資料")
  public ResponseEntity<Outbound> updateUserProfile(@Valid @RequestBody UpdateUserReq request)
      throws Exception {
    return ResponseEntity.ok(authService.updateUserProfile(request.getUsername(), request));
  }

  @PostMapping("/change-password")
  @Operation(summary = "修改密碼")
  public ResponseEntity<Outbound> changePassword(@Valid @RequestBody ChangePswReq request)
      throws Exception {
     return ResponseEntity.ok(authService.updatePassword(request));
  }
}
