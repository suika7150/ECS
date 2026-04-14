package com.gjun.ecs.scheduler;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import com.gjun.ecs.entity.Order;
import com.gjun.ecs.entity.OrderItem;
import com.gjun.ecs.repository.OrderRepository;
import com.gjun.ecs.repository.ProductRepository;

import lombok.extern.slf4j.Slf4j;


@Component
@Slf4j
public class OrderScheduler {
    
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;


    @Scheduled(fixedRate = 10000) 
    @Transactional 
    public void releaseUnpaidOrders() {

        // 排程:更改未付款訂單狀態為 "expired"
        // 回滾未付款訂單的庫存
        // 定義逾時臨界點：現在時間減去 1 分鐘
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(60);
        
        List<Order> expiredOrders = orderRepository.findByPaymentStatusAndCreatedAtBefore("pending", threshold);
        
        if(expiredOrders.isEmpty()){
        
            return;
        }

        log.info("發現 {} 筆逾時未付款訂單，準備執行庫存回滾...", expiredOrders.size());

        for (Order order : expiredOrders) {
            try {
                // 1. 遍歷訂單明細，將庫存加回去
                for (OrderItem item : order.getItems()) {
                    productRepository.updateStock(item.getProductId(), item.getQuantity());
                    log.info("訂單 {}：商品 ID {} 庫存已補回 {}", order.getId(), item.getProductId(), item.getQuantity());
                }

                // 2. 將訂單狀態改為 "expired" 或 "cancelled"
                order.setPaymentStatus("expired");
                orderRepository.save(order);
                
            } catch (Exception e) {
                log.error("處理訂單 {} 回滾時發生錯誤: {}", order.getId(), e.getMessage());
            }
        }
    }
}
