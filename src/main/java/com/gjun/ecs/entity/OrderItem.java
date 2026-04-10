package com.gjun.ecs.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Table(name = "order_items")
public class OrderItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // 指向所屬的訂單
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(name="product_id")
    private Integer productId;
    
    @Column(name = "product_name")
    private String productName; // 商品名稱 (快照，防止商品改名後找不到當時買什麼)

    @Column(name = "quantity", nullable = false)
    private Integer quantity; // 購買數量

    @Column(name = "price", nullable = false)
    private Integer price; // 購買時單價

}
