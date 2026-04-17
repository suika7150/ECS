package com.gjun.ecs.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gjun.ecs.dto.response.Outbound;
import com.gjun.ecs.entity.RefreshToken;
import com.gjun.ecs.entity.UserInfo;
import com.gjun.ecs.enums.ResultCode;
import com.gjun.ecs.exception.ApplicationException;
import com.gjun.ecs.repository.TokenRepository;
import com.gjun.ecs.utils.JwtUtil;
import jakarta.transaction.Transactional;

@Service
public class TokenService {

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Transactional
    public Outbound refreshToken(String refreshTokenStr) throws ApplicationException{

        // 透過 JPA 從資料庫尋找對應的 Refresh Token
        RefreshToken refreshToken = tokenRepository.findByToken(refreshTokenStr)
                .orElseThrow(()-> new ApplicationException(ResultCode.USER_STATUS_ERROR));

        // 檢查 Token 是否過期了
        if(refreshToken.getExpiryDate().isBefore(LocalDateTime.now())){
            tokenRepository.delete(refreshToken); // 過期刪掉(節省空間)
            throw new ApplicationException(ResultCode.USER_STATUS_ERROR);
        
        }

        // 透過關聯的 UserInfo 檢查使用者狀態
        UserInfo userInfo = refreshToken.getUserInfo();
        if(userInfo.getStatus() != 1){

            throw new ApplicationException(ResultCode.USER_STATUS_ERROR);

        }

        // 產生新的 Access Token (短效 Token)
        String newAccessToken = jwtUtil.generateToken(userInfo,true);

        String newRefreshTokenStr = java.util.UUID.randomUUID().toString();

        
        // 組裝回傳結果
        Map<String, Object> result = new HashMap<>();
        result.put("token", newAccessToken);
        result.put("refreshToken", newRefreshTokenStr);
    
        return Outbound.ok(result);

    }
}
