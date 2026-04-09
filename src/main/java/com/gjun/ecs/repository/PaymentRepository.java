package com.gjun.ecs.repository;

import org.springframework.stereotype.Repository;
import com.gjun.ecs.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;


@Repository
public interface PaymentRepository extends JpaRepository<Payment,Long>{

    Payment findByMerchantTradeNo(String merchantTradeNo);    

}
