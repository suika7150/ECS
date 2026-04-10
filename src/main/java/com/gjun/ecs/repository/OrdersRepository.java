package com.gjun.ecs.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.gjun.ecs.entity.Order;

public interface OrdersRepository extends JpaRepository<Order,Long>{
    
    // 排程器查詢，找出逾期未付款訂單
    // SELECT * FROM orders WHERE payment_status = ? AND created_at < ?
    List<Order> findByPaymentStatusAndCreatedAtBefore(String status, LocalDateTime time);

    // 獲取所有訂單並依照 ID 倒序排列
    List<Order> findAllByOrderByIdDesc();

}
