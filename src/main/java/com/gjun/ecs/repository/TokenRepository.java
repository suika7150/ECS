package com.gjun.ecs.repository;

import com.gjun.ecs.entity.RefreshToken;
import com.gjun.ecs.entity.UserInfo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends JpaRepository<RefreshToken,Long> {

    // 根據 Token 字串尋找紀錄
    Optional<RefreshToken> findByToken(String token);

    // 根據使用者尋找 Token，用於登入時刪除舊的 Token
    // Optional<RefreshToken> findByUser(UserInfo userInfo);

    // 登出或過期時刪除 Token
    void deleteByToken(String token);

    // 刪除使用者的舊 Token 方便 AuthService 呼叫
    void deleteByUserInfo(UserInfo userInfo);
}
