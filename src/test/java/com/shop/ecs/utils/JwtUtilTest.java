package com.shop.ecs.utils;

import com.shop.ecs.entity.UserEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    // 資料庫配置
    "spring.datasource.url=jdbc:mysql://localhost:3306/ecs_sit?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true",
    "spring.datasource.username=root",
    "spring.datasource.password=1234",
    "spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver",
    "spring.jpa.hibernate.ddl-auto=update",
    
    // 其他元件啟動所需的假環境變數
    "JWT_SECRET=mock-jwt-secret-key-must-be-very-long-for-hmac-sha-otherwise-it-will-throw-exception-123456",
    "RECAPTCHA_SITE_KEY=mock-site-key",
    "RECAPTCHA_SECRET_KEY=mock-secret-key",
    "MAIL_USERNAME=mock-mail@example.com",
    "MAIL_PASSWORD=mock-password"
})
class JwtUtilTest {

    @Autowired
    private JwtUtil jwtUtil;

    @Test
    @DisplayName("驗證 JWT Token 生成、解析與有效性驗證")
    void testJwtFullLifecycle() {
        // Arrange：準備測試資料
        UserEntity mockUser = new UserEntity();
        mockUser.setUsername("test-user@example.com"); 

        // Act：執行目標方法
        String token = jwtUtil.generateToken(mockUser, false);
        String extractedUsername = jwtUtil.getUsernameFromToken(token);
        boolean isValid = jwtUtil.validateToken(token, mockUser);

        // Assert：驗證結果是否正確
        assertNotNull(token, "產生的 JWT Token 不應為空");
        assertTrue(token.startsWith("ey"), "JWT Token 格式不正確（應以 ey 開頭）");
        assertEquals("test-user@example.com", extractedUsername, "解析出的使用者名稱與預期不符");
        assertTrue(isValid, "合法的 Token 驗證結果應為 true");
    }
}
