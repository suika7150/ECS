package com.shop.ecs.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.shop.ecs.service.ProductService;

@Component
@Slf4j
public class ProductClearScheduler {

  @Autowired
  private ProductService productService;

  @Value("${shop.product.clear-days-ago}")
  private int clearDaysAgo;

  // 每周一凌晨 3 點清理過期的已刪除商品
  @Scheduled(cron = "0 0 3 ? * 1")
  public void clearExpiredDeletedProducts() {
    log.info("====== [排程啟動] 開始清理過期的已刪除商品 ======");

    try {
      int deletedCount = productService.clearExpiredProducts(clearDaysAgo);

      log.info("====== [排程結束] 清理完成，共從資料庫永久硬刪除 {} 筆過期商品 ======", deletedCount);
    } catch (Exception error) {
      log.error("====== [排程異常] 清理過期商品時發生錯誤: {} ======", error.getMessage(), error);
    }
  }
}
