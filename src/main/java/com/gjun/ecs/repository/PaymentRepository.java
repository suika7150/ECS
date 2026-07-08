package com.gjun.ecs.repository;

import com.gjun.ecs.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

  Payment findByMerchantTradeNo(String merchantTradeNo);
}
