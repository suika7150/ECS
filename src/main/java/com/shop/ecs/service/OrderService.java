package com.shop.ecs.service;

import com.shop.ecs.common.result.Outbound;
import com.shop.ecs.constant.OrderStatusEnum;
import com.shop.ecs.constant.PaymentStatusEnum;
import com.shop.ecs.dto.request.OrderReq;
import com.shop.ecs.dto.response.EcpayParamsResp;
import com.shop.ecs.dto.response.OrderItemResp;
import com.shop.ecs.dto.response.OrderResp;
import com.shop.ecs.entity.OrderEntity;
import com.shop.ecs.repository.OrderRepository;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 購物車結帳訂單 */
@Service
public class OrderService {

  @Autowired
  private OrderRepository orderRepository;

  @Autowired
  private OrderTxService orderTxService;

  @Autowired
  private EcpayService ecpayService;

  private static final Logger log = LoggerFactory.getLogger(OrderService.class);

  // 建立訂單
  @Transactional(rollbackFor = Exception.class)
  public Outbound createOrder(OrderReq req) {
    log.info("建立訂單請求 : {}", req);

    OrderEntity newOrder = orderTxService.executeOrderCreation(req);
    log.info("訂單建立成功: ID={}，姓名={}", newOrder.getId(), newOrder.getName());
    
    // 產生綠界參數
    EcpayParamsResp ecpayParams = ecpayService.createPayment(newOrder);

    // 更新交易編號
    newOrder.setMerchantTradeNo(ecpayParams.getMerchantTradeNo());
    orderRepository.save(newOrder);

    // 回傳綠界參數
    OrderResp resp = convertToOrderResp(newOrder);
    resp.setMerchantTradeNo(ecpayParams.getMerchantTradeNo());
    resp.setEcpayParams(ecpayParams);
    return Outbound.ok(resp);
  }

  // 查詢使用者訂單列表
  @Transactional(readOnly = true)
  public Outbound  getUserOrders(OrderStatusEnum orderStatus) {
    log.info("收到訂單查詢，status={}", orderStatus);
    List<OrderEntity> orders;

    if (orderStatus == null) {
      orders = orderRepository.findAllByOrderByIdDesc();
    } else {
      orders = orderRepository.findByOrderStatusWithItemsOrderByIdDesc(orderStatus);
    }

    log.info("查詢完成，結果筆數: {}", orders.size());

    List<OrderResp> resp = orders.stream()
        .map(order -> this.convertToOrderResp(order))
        .toList();
    
    return Outbound.ok(resp);
  }

  // 查詢訂單明細
  @Transactional(readOnly = true)
  public Outbound getOrderDetail(String orderId) {

    OrderEntity order = orderRepository
        .findByIdWithItems(Long.parseLong(orderId))
        .orElseThrow(() -> new RuntimeException("找不到該筆訂單"));

    OrderResp resp = convertToOrderResp(order);  
    return Outbound.ok(resp);
  }

  private OrderResp convertToOrderResp(OrderEntity order) {

    OrderResp resp = OrderResp.builder()
        .id(order.getId())
        .name(order.getName())
        .phone(order.getPhone())
        .address(order.getAddress())
        .shippingMethod(order.getShippingMethod())
        .shippingFee(order.getShippingFee())
        .notes(order.getNotes())
        .paymentMethod(order.getPaymentMethod())
        .createdAt(order.getCreatedAt())
        .discount(order.getDiscount())
        .total(order.getTotal())
        .orderStatus(order.getOrderStatus())
        .paymentStatus(order.getPaymentStatus())
        .shippingStatus(order.getShippingStatus())
        .merchantTradeNo(order.getMerchantTradeNo())
        .build();

    if (order.getItems() != null) {

      List<OrderItemResp> itemResps = order.getItems().stream()
          .map(
              item -> OrderItemResp.builder()
                  .name(item.getProductName())
                  .price(item.getPrice())
                  .quantity(item.getQuantity())
                  .productImage(item.getProductImage())
                  .build())
          .toList();

      resp.setItems(itemResps);
    }

    return resp;
  }

  @Transactional(rollbackFor = Exception.class)
  public void updatePaymentResult(long orderId, boolean success) {

    OrderEntity order = orderRepository.findById(orderId)
        .orElseThrow(() -> new RuntimeException("找不到訂單"));

    // 防重複 callback
    if (order.getPaymentStatus() == PaymentStatusEnum.PAID) {
      log.info("訂單已付款完成，略過重複 callback，orderId={}", orderId);
      return;
    }

    if (success) {
      order.setPaymentStatus(PaymentStatusEnum.PAID);
      order.setOrderStatus(OrderStatusEnum.PROCESSING);
    } else {
      order.setPaymentStatus(PaymentStatusEnum.FAILED);
      order.setOrderStatus(OrderStatusEnum.CANCELLED);
    }

    orderRepository.save(order);
  }
}
