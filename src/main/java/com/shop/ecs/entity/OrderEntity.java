package com.shop.ecs.entity;

import jakarta.persistence.*;
import java.util.List;

import com.shop.ecs.enums.OrderStatus;
import com.shop.ecs.enums.PaymentMethod;
import com.shop.ecs.enums.PaymentStatus;
import com.shop.ecs.enums.ShippingStatus;

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
public class OrderEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "name", nullable = false)
  private String name; // 姓名

  @Column(name = "phone", nullable = false)
  private String phone; // 電話

  @Column(name = "address", nullable = false)
  private String address; // 地址

  @Column(name = "shipping_method", nullable = false)
  private String shippingMethod; // 運送方式

  @Column(name = "shipping_fee", nullable = false)
  private Integer shippingFee; // 運費

  @Column(name = "notes", nullable = true)
  private String notes; // 備註

  @Enumerated(EnumType.STRING)
  @Column(name = "payment_method", nullable = false)
  private PaymentMethod paymentMethod; // 付款方式

  @Column(name = "coupon_code", length = 50)
  private String couponCode; // 優惠券代碼

  @Builder.Default
  @Column(name = "discount", nullable = false)
  private Integer discount = 0; // 優惠券折扣金額

  @Column(name = "total", nullable = false)
  private Integer total; // 總金額

  @Builder.Default
  @Enumerated(EnumType.STRING)
  @Column(name = "order_status", nullable = false)
  private OrderStatus orderStatus = OrderStatus.PENDING_PAYMENT;

  @Builder.Default
  @Enumerated(EnumType.STRING)
  @Column(name = "payment_status", nullable = false)
  private PaymentStatus paymentStatus = PaymentStatus.UNPAID;

  @Builder.Default
  @Enumerated(EnumType.STRING)
  @Column(name = "shipping_status", nullable = false)
  private ShippingStatus shippingStatus = ShippingStatus.NOT_SHIPPED;;

  @Column(name = "created_at", updatable = false, columnDefinition = "DATETIME(0)")
  private java.time.LocalDateTime createdAt;

  @jakarta.persistence.PrePersist
  protected void onCreate() {
    createdAt = java.time.LocalDateTime.now();
  }

  @Column(name = "merchant_trade_no", length = 50)
  private String merchantTradeNo; // 交易編號

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<OrderItemEntity> items; // 訂單明細
}
