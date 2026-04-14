package com.gjun.ecs.enums;

/* 訂單狀態 */ 
public enum OrderStatus {
    PENDING,    // 待付款
    PAID,       // 處理中 (已付款)
    SHIPPED,    // 已出貨
    COMPLETED,  // 已完成
    CANCELLED   // 已取消
}