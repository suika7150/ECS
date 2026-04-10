package com.gjun.ecs.entity;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name="name",nullable=false)
    private String name; // 姓名

    @Column(name="phone",nullable=false)
    private String phone; // 電話

    @Column(name="address",nullable=false)
    private String address; // 地址

    @Column(name="shipping_method",nullable=false)
    private String shippingMethod; // 運送方式

    @Column(name="notes",nullable=true)
    private String notes; // 備註

    @Column(name="payment_method",nullable=false)
    private String paymentMethod; // 付款方式

    @Column(name="coupon_code",length=50)
    private String couponCode; // 優惠券代碼
    
    @Builder.Default
    @Column(name="discount",nullable = false)
    private Integer discount = 0; // 優惠券折扣金額

    @Column(name="total",nullable=false)
    private Integer total; // 總金額

    @Column(name="card_last4",length=4)
    private String cardLast4; // 信用卡最後四碼

    @Builder.Default
    @Column(name="payment_status", nullable = false)
    private String paymentStatus = "pending"; // 付款狀態

    @Column(name="created_at",updatable=false)
    private java.time.LocalDateTime createdAt;

    @jakarta.persistence.PrePersist
    protected void onCreate() {
        createdAt = java.time.LocalDateTime.now();
    }

    @Column(name = "merchant_trade_no" , length = 50)
    private String merchantTradeNo ; // 交易編號


    @OneToMany(mappedBy = "order", cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true, fetch = jakarta.persistence.FetchType.EAGER)
    private List<OrderItem> items; // 訂單明細
}
