package com.shop.ecs.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shop.ecs.entity.PaymentEntity;

public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {

  PaymentEntity findByMerchantTradeNo(String merchantTradeNo);
}
