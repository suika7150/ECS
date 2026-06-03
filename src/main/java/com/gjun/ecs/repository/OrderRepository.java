package com.gjun.ecs.repository;

import com.gjun.ecs.entity.Order;
import com.gjun.ecs.enums.OrderStatus;
import com.gjun.ecs.enums.PaymentStatus;
import com.gjun.ecs.enums.ShippingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderRepository extends JpaRepository<Order, Long> {

  // 排程器查詢，找出逾期未付款訂單
  // SELECT * FROM orders WHERE payment_status = ? AND created_at < ?
  List<Order> findByPaymentStatusAndCreatedAtBefore(PaymentStatus status, LocalDateTime time);

  // 獲取所有訂單並依照 ID 倒序排列
  @Query("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.items ORDER BY o.id DESC") // 防止 N+1 問題
  List<Order> findAllByOrderByIdDesc();

  List<Order> findByPaymentStatusOrderByIdDesc(PaymentStatus paymentStatus);

  List<Order> findByOrderStatusOrderByIdDesc(OrderStatus orderStatus);

  List<Order> findByShippingStatusOrderByIdDesc(ShippingStatus shippingStatus);

  @Query("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.items WHERE o.orderStatus = :orderStatus ORDER BY o.id DESC")
  List<Order> findByOrderStatusWithItemsOrderByIdDesc(@Param("orderStatus") OrderStatus orderStatus);

  @Query("SELECT o FROM Order o LEFT JOIN FETCH o.items WHERE o.id = :id")
  Optional<Order> findByIdWithItems(@Param("id") Long id);
}
