package com.gjun.ecs.enums;

/* 訂單狀態 */
public enum OrderStatus {
  PENDING_PAYMENT, // 已建立訂單（待付款）
  PROCESSING, // 已付款（進入處理）
  SHIPPED, // 已出貨
  COMPLETED, // 已完成
  CANCELLED // 已取消
}
