package com.gjun.ecs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.gjun.ecs.entity.Order;

public interface OrdersRepository extends JpaRepository<Order,Long>{
    
}
