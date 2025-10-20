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
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name="name",nullable=false)
    private String name;

    @Column(name="phone",nullable=false)
    private String phone;

    @Column(name="address",nullable=false)
    private String address;

    @Column(name="shipping_method",nullable=false)
    private String shippingMethod;

    @Column(name="notes",nullable=true)
    private String notes;

    @Column(name="payment_method",nullable=false)
    private String paymentMethod;

    @Column(name="total",nullable=false)
    private Integer total;
}
