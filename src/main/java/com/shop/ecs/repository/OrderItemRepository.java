package com.shop.ecs.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import com.shop.ecs.entity.OrderItemEntity;

public interface OrderItemRepository extends JpaRepository<OrderItemEntity, Long> {

  List<OrderItemEntity> findByOrderId(Long orderId);
}
