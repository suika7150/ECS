package com.gjun.ecs.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "payment")
public class Payment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // 必須跟綠界的 TotalAmount 對應 (整數)
    private Integer totalAmount;

    // 綠界的特店交易編號
    @Column(name = "merchant_trade_no" , unique = true)
    private String merchantTradeNo;

    // 付款狀態 (0:未付, 1:已付, 2:失敗)
    @Builder.Default
    @Column(name = "rtn_code")
    private String rtnCode = "0";

    // 紀錄綠界回傳的詳細訊息
    @Column(name = "rtn_msg")
    private String rtnMsg;

    // 紀錄實際付款時間
    @Column(name = "payment_date")
    private String paymentDate;
}
