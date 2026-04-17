package com.gjun.ecs.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gjun.ecs.dto.response.Outbound;
import com.gjun.ecs.enums.ResultCode;
import com.gjun.ecs.exception.ApplicationException;
import com.gjun.ecs.service.TokenService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/token")
public class TokenController {
  
    @Autowired
    private TokenService tokenService;
  
    /**
   * 刷新 Token 
   * 從 Cookie 讀取 Refresh Token，驗證後發放新的 Access Token
   */
  @PostMapping("/refresh")
  @Operation(summary = "刷新 Token", description = "使用 Refresh Token 獲取新的 Access Token")
  public ResponseEntity<Outbound> refreshToken(HttpServletRequest request) throws ApplicationException{
    
    // 從 Header 取得 Refresh Token
    String refreshToken = request.getHeader("X-Refresh-Token");

    if(refreshToken == null || refreshToken.isEmpty()){
        throw new ApplicationException(ResultCode.UNAUTHORIZED);
    }
    Outbound response = tokenService.refreshToken(refreshToken);
    
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }
}
