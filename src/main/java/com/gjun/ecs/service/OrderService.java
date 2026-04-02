package com.gjun.ecs.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gjun.ecs.dto.request.OrderReq;
import com.gjun.ecs.dto.response.OrderResp;
import com.gjun.ecs.entity.Order;
import com.gjun.ecs.entity.Product;
import com.gjun.ecs.repository.OrdersRepository;
import com.gjun.ecs.repository.ProductRepository;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
    購物車結帳訂單
 **/

@Service
public class OrderService{

    @Autowired
    private OrdersRepository ordersRepository;
    @Autowired
    private ProductRepository productRepository;
    
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    @Transactional
    public OrderResp createOrder(OrderReq req) {

        log.info("建立訂單請求 : {}",req);

        // 檢查商品庫存並更新庫存
        for(OrderReq.Item item : req.getItems()) {
            Product product = productRepository.findByIdForUpdate(item.getProductId());

            if(product == null) {
                log.warn("商品不存在 ID: {}", item.getProductId());
                throw new RuntimeException("商品不存在" + item.getProductId());
            }

            log.info("🛒 商品名稱={} | ID={} | 原庫存={} | 訂購數量={}",
                 product.getName(), product.getId(), product.getStock(), item.getQuantity());

            if(product.getStock() < item.getQuantity()) {
             log.warn("❌ 庫存不足：{} (剩餘 {}，訂購 {})",
                     product.getName(), product.getStock(), item.getQuantity());
                throw new RuntimeException("商品庫存不足: " + product.getName());
            }
            product.setStock(product.getStock() - item.getQuantity());
            productRepository.save(product);
        
            log.info("✅ 庫存更新後：{} (剩餘 {}，訂購 {})",
                 product.getName(), product.getStock(), item.getQuantity());

        }

        Order orders = Order.builder()
            .name(req.getName())
            .phone(req.getPhone())
            .address(req.getAddress())
            .shippingMethod(req.getShippingMethod())
            .notes(req.getNotes())
            .paymentMethod(req.getPaymentMethod())
            .couponCode(req.getCouponCode())
            .discount(req.getDiscount())
            .total(req.getTotal())
            .cardLast4(req.getCardLast4())
            .paymentStatus(req.getPaymentStatus())
            .build();
            
            Order newOrder = ordersRepository.save(orders);
            
            log.info("訂單建立成功: ID={}，姓名={}", newOrder.getId(), newOrder.getName());

            // 回傳訂單資訊
            return OrderResp.builder()
                .id(newOrder.getId())
                .name(newOrder.getName())
                .phone(newOrder.getPhone())
                .address(newOrder.getAddress())
                .shippingMethod(newOrder.getShippingMethod())
                .notes(newOrder.getNotes())
                .paymentMethod(newOrder.getPaymentMethod())
                .couponCode(req.getCouponCode())
                .discount(req.getDiscount())
                .total(newOrder.getTotal())
                .cardLast4(req.getCardLast4())
                .paymentStatus(req.getPaymentStatus())
                .build();

    }
}