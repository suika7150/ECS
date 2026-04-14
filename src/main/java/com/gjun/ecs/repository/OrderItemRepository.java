package com.gjun.ecs.repository;

import java.util.List;

import com.gjun.ecs.entity.Order;
import com.gjun.ecs.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    List <OrderItem> findByOrderId(Long orderId);
    
}