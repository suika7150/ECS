package com.shop.ecs.controller;

import com.shop.ecs.dto.request.OrderReq;
import com.shop.ecs.dto.response.Outbound;
import com.shop.ecs.enums.OrderStatus;
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
@RequestMapping("/api")
@Tag(name = "OrderEntity", description = "訂單相關API")
public class OrderController {

  @Autowired
  private OrderService orderService;

  @PostMapping("/orders")
  @Operation(summary = "建立訂單")
  public ResponseEntity<Outbound> createOrder(@RequestBody OrderReq orderReq) {
    return ResponseEntity.ok(Outbound.ok(orderService.createOrder(orderReq)));
  }

  @GetMapping("/orders")
  @Operation(summary = "獲取目前登入使用者的訂單列表")
  public ResponseEntity<Outbound> getOrderList(@RequestParam(required = false) OrderStatus status) {
    return ResponseEntity.ok(Outbound.ok(orderService.getUserOrders(status)));
  }

  @GetMapping("/orders/{orderId}")
  @Operation(summary = "獲取訂單詳情")
  public ResponseEntity<Outbound> getOrderDetail(@PathVariable String orderId) {
    return ResponseEntity.ok(Outbound.ok(orderService.getOrderDetail(orderId)));
  }
}
