package com.gjun.ecs.enums;

public enum OtpType {
    REGISTER,   // 註冊用（email 不可存在）
    RESET       // 忘記密碼用（email 必須存在）
}