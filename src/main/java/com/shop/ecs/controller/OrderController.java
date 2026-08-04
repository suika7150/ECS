package com.shop.ecs.controller;

import com.shop.ecs.common.result.Outbound;
import com.shop.ecs.constant.OrderStatusEnum;
import com.shop.ecs.dto.request.OrderReq;
import com.shop.ecs.service.OrderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
@Tag(name = "訂單管理 API", description = "訂單建立、查詢、詳情")
public class OrderController {

  @Autowired
  private OrderService orderService;

  @PostMapping
  @Operation(summary = "建立訂單")
  public ResponseEntity<Outbound> createOrder(@RequestBody OrderReq orderReq) {
    return ResponseEntity.ok(orderService.createOrder(orderReq));
  }

  @GetMapping
  @Operation(summary = "獲取目前登入使用者的訂單列表", description = "獲取目前登入使用者的訂單列表，可透過訂單狀態進行篩選")
  public ResponseEntity<Outbound> getOrderList(@RequestParam(required = false) OrderStatusEnum status) {
    return ResponseEntity.ok(orderService.getUserOrders(status));
  }

  @GetMapping("/{orderId}")
  @Operation(summary = "獲取訂單詳情", description = "根據訂單 ID 獲取該筆訂單的詳細內容")
  public ResponseEntity<Outbound> getOrderDetail(@PathVariable String orderId) {
    return ResponseEntity.ok(orderService.getOrderDetail(orderId));
  }
}
