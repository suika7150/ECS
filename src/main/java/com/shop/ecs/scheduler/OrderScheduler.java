package com.shop.ecs.scheduler;

import com.shop.ecs.constant.OrderStatusEnum;
import com.shop.ecs.constant.PaymentStatusEnum;
import com.shop.ecs.entity.OrderEntity;
import com.shop.ecs.entity.OrderItemEntity;
import com.shop.ecs.repository.OrderRepository;
import com.shop.ecs.repository.ProductRepository;

import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
public class OrderScheduler {

  @Autowired
  private OrderRepository orderRepository;

  @Autowired
  private ProductRepository productRepository;

  @Scheduled(fixedRate = 10000) // 每小時執行一次
  @Transactional
  public void releaseUnpaidOrders() {

    // 排程:更改未付款訂單狀態為 "expired"
    // 回滾未付款訂單的庫存
    // 定義逾時臨界點：現在時間減去 1 分鐘
    LocalDateTime threshold = LocalDateTime.now().minusMinutes(15);

    List<OrderEntity> expiredOrders = orderRepository.findByPaymentStatusAndCreatedAtBefore(PaymentStatusEnum.UNPAID, threshold);

    if (expiredOrders.isEmpty()) {

      return;
    }

    log.info("發現 {} 筆逾時未付款訂單，準備執行庫存回滾...", expiredOrders.size());

    for (OrderEntity order : expiredOrders) {
      try {
        // 遍歷訂單明細，將庫存加回去
        for (OrderItemEntity item : order.getItems()) {
          productRepository.updateStock(item.getProductId(), item.getQuantity());
          log.info(
              "訂單 {}：商品 ID {} 庫存已補回 {}", order.getId(), item.getProductId(), item.getQuantity());
        }

        order.setPaymentStatus(PaymentStatusEnum.FAILED);
        order.setOrderStatus(OrderStatusEnum.CANCELLED);
        orderRepository.save(order);

      } catch (Exception e) {
        log.error("處理訂單 {} 回滾時發生錯誤: {}", order.getId(), e.getMessage());
      }
    }
  }
}
