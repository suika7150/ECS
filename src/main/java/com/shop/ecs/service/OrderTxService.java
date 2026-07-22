package com.shop.ecs.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shop.ecs.constant.OrderStatusEnum;
import com.shop.ecs.constant.PaymentStatusEnum;
import com.shop.ecs.constant.ShippingStatusEnum;
import com.shop.ecs.dto.request.OrderReq;
import com.shop.ecs.entity.OrderEntity;
import com.shop.ecs.entity.OrderItemEntity;
import com.shop.ecs.entity.ProductEntity;
import com.shop.ecs.repository.OrderRepository;
import com.shop.ecs.repository.ProductRepository;
import com.shop.ecs.utils.ImageUtils;

@Service
public class OrderTxService {
    
  @Autowired
  private OrderRepository orderRepository;

  @Autowired
  private ProductRepository productRepository;
  
  private static final Logger log = LoggerFactory.getLogger(OrderTxService.class);
  
  // 訂單流程
  @Transactional(rollbackFor = Exception.class)
  public OrderEntity executeOrderCreation(OrderReq req) {

    OrderEntity order = OrderEntity.builder()
        .name(req.getName())
        .phone(req.getPhone())
        .address(req.getAddress())
        .shippingMethod(req.getShippingMethod())
        .shippingFee(req.getShippingFee() == null ? 0 : req.getShippingFee())
        .notes(req.getNotes() == null || req.getNotes().isBlank() ? "None" : req.getNotes())
        .paymentMethod(req.getPaymentMethod())
        .couponCode(req.getCouponCode())
        .discount(req.getDiscount() == null ? 0 : req.getDiscount())
        .total(req.getTotal())
        .orderStatus(OrderStatusEnum.PENDING_PAYMENT)
        .paymentStatus(PaymentStatusEnum.UNPAID)
        .shippingStatus(ShippingStatusEnum.NOT_SHIPPED)
        .build();

    List<OrderItemEntity> itemList = new ArrayList<>();

    for (OrderReq.Item item : req.getItems()) {
      // 悲觀鎖鎖定商品
      ProductEntity product = productRepository.findByIdForUpdate(item.getProductId());

      if (product == null) {
        throw new RuntimeException("商品不存在" + item.getProductId());
      }

      if (product.getStock() < item.getQuantity()) {
        throw new RuntimeException("商品庫存不足: " + product.getName());
      }

      product.setStock(product.getStock() - item.getQuantity());
      productRepository.save(product); // 更新庫存

      log.info("[庫存變更] 商品ID: {}, 原庫存: {}, 扣減數: {}, 剩餘: {}", 
                product.getId(), product.getStock() + item.getQuantity(), item.getQuantity(), product.getStock());

      OrderItemEntity orderItem = 
        OrderItemEntity.builder()
          .order(order)
          .productId(item.getProductId())
          .productName(product.getName())
          .price(product.getPrice())
          .quantity(item.getQuantity())
          .productImage(ImageUtils.toBase64Src(product.getImageData(), product.getImageType()))
          .build();

      itemList.add(orderItem);
    }

    order.setItems(itemList);
    OrderEntity orderEntity = orderRepository.save(order);
    return orderEntity;
  }
}
