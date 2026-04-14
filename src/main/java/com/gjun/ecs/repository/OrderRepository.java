package com.gjun.ecs.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.gjun.ecs.entity.Order;

public interface OrderRepository extends JpaRepository<Order,Long>{
    
    // 排程器查詢，找出逾期未付款訂單
    // SELECT * FROM orders WHERE payment_status = ? AND created_at < ?
    List<Order> findByPaymentStatusAndCreatedAtBefore(String status, LocalDateTime time);

    // 獲取所有訂單並依照 ID 倒序排列
    @Query("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.items ORDER BY o.id DESC")
    List<Order> findAllByOrderByIdDesc();

    // 根據支付狀態查詢並按 ID 降序排列
    @Query("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.items WHERE o.paymentStatus = :status ORDER BY o.id DESC")
    List<Order> findByPaymentStatusOrderByIdDesc(@Param("status")String status);

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.items WHERE o.id = :id")
    Optional<Order> findByIdWithItems(@Param("id") Long id);

}
