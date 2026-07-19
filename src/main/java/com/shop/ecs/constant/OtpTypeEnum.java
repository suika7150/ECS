package com.shop.ecs.constant;

public enum OtpTypeEnum {
    REGISTER, // 註冊用（email 不可存在）
    RESET, // 忘記密碼用（email 必須存在）
    LOGIN // 登入用（2FA 驗證）
}