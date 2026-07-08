package com.shop.ecs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shop.ecs.entity.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

  Payment findByMerchantTradeNo(String merchantTradeNo);
}
