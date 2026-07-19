package com.shop.ecs.repository;

import com.shop.ecs.constant.OrderStatusEnum;
import com.shop.ecs.constant.PaymentStatusEnum;
import com.shop.ecs.constant.ShippingStatusEnum;
import com.shop.ecs.entity.OrderEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

  // 排程器查詢，找出逾期未付款訂單
  // SELECT * FROM orders WHERE payment_status = ? AND created_at < ?
  List<OrderEntity> findByPaymentStatusAndCreatedAtBefore(PaymentStatusEnum status, LocalDateTime time);

  // 獲取所有訂單並依照 ID 倒序排列
  @Query("SELECT DISTINCT o FROM OrderEntity o LEFT JOIN FETCH o.items ORDER BY o.id DESC") // 防止 N+1 問題
  List<OrderEntity> findAllByOrderByIdDesc();

  List<OrderEntity> findByPaymentStatusOrderByIdDesc(PaymentStatusEnum paymentStatus);

  List<OrderEntity> findByOrderStatusOrderByIdDesc(OrderStatusEnum orderStatus);

  List<OrderEntity> findByShippingStatusOrderByIdDesc(ShippingStatusEnum shippingStatus);

  @Query("SELECT DISTINCT o FROM OrderEntity o LEFT JOIN FETCH o.items WHERE o.orderStatus = :orderStatus ORDER BY o.id DESC")
  List<OrderEntity> findByOrderStatusWithItemsOrderByIdDesc(@Param("orderStatus") OrderStatusEnum orderStatus);

  @Query("SELECT o FROM OrderEntity o LEFT JOIN FETCH o.items WHERE o.id = :id")
  Optional<OrderEntity> findByIdWithItems(@Param("id") Long id);
}
