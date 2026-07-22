package com.shop.ecs.service;

import com.shop.ecs.common.result.Outbound;
import com.shop.ecs.constant.OrderStatusEnum;
import com.shop.ecs.constant.PaymentStatusEnum;
import com.shop.ecs.constant.ShippingStatusEnum;
import com.shop.ecs.dto.request.OrderReq;
import com.shop.ecs.dto.response.EcpayParamsResp;
import com.shop.ecs.dto.response.OrderItemResp;
import com.shop.ecs.dto.response.OrderResp;
import com.shop.ecs.entity.OrderEntity;
import com.shop.ecs.entity.OrderItemEntity;
import com.shop.ecs.entity.ProductEntity;
import com.shop.ecs.repository.OrderRepository;
import com.shop.ecs.repository.ProductRepository;
import com.shop.ecs.utils.ImageUtils;

import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** 購物車結帳訂單 */
@Service
public class OrderService {

  @Autowired
  private OrderRepository orderRepository;
  @Autowired
  private ProductRepository productRepository;
  @Autowired
  private EcpayService ecpayService;

  private static final Logger log = LoggerFactory.getLogger(OrderService.class);

  @Transactional
  public Outbound createOrder(OrderReq req) {
    log.info("建立訂單請求 : {}", req);


    OrderEntity orders = OrderEntity.builder()
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
      // 悲觀鎖查詢
      ProductEntity product = productRepository.findByIdForUpdate(item.getProductId());

      if (product == null) {
        throw new RuntimeException("商品不存在" + item.getProductId());
      }

      // 檢查並扣庫存
      if (product.getStock() < item.getQuantity()) {
        throw new RuntimeException("商品庫存不足: " + product.getName());
      }

      product.setStock(product.getStock() - item.getQuantity());
      productRepository.save(product); // 更新庫存

      log.debug("[庫存變更] 商品ID: {}, 原庫存: {}, 扣減數: {}, 剩餘: {}", 
                product.getId(), product.getStock() + item.getQuantity(), item.getQuantity(), product.getStock());

      OrderItemEntity orderItem = OrderItemEntity.builder()
          .order(orders)
          .productId(item.getProductId())
          .productName(product.getName())
          .price(product.getPrice())
          .quantity(item.getQuantity())
          .productImage(ImageUtils.toBase64Src(product.getImageData(), product.getImageType()))
          .build();

      itemList.add(orderItem);
    }

    orders.setItems(itemList);
    OrderEntity newOrder = orderRepository.save(orders);
    log.info("訂單建立成功: ID={}，姓名={}", newOrder.getId(), newOrder.getName());

    // 呼叫 PaymentService 產生綠界參數
    EcpayParamsResp ecpayParams = ecpayService.createPayment(newOrder);

    // 將編號存回 newOrder 並再次存檔
    newOrder.setMerchantTradeNo(ecpayParams.getMerchantTradeNo());
    orderRepository.save(newOrder);

    // 將綠界參數回傳
    OrderResp resp = convertToOrderResp(newOrder);
    resp.setEcpayParams(ecpayParams);
    return Outbound.ok(resp);
  }

  // 查詢使用者訂單列表
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

  @Transactional
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
