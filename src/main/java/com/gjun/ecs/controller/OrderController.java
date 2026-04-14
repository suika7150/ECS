package com.gjun.ecs.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gjun.ecs.dto.request.OrderReq;
import com.gjun.ecs.dto.response.OrderResp;
import com.gjun.ecs.dto.response.Outbound;
import com.gjun.ecs.service.OrderService;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;


@RestController
@RequestMapping("/api")
@Tag(name = "Order", description = "訂單相關API")
public class OrderController {
    
    @Autowired
    private OrderService orderService;
    
    @PostMapping(path = "/orders", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "建立訂單")
    public ResponseEntity<Outbound> createOrder(
        @RequestBody OrderReq orderReq){
        OrderResp resp = orderService.createOrder(orderReq);
        return ResponseEntity.ok(Outbound.ok(resp));
        
    }
    @GetMapping("/orderList")
    @Operation(summary = "獲取目前登入使用者的訂單列表")
    public ResponseEntity<Outbound> getOrderList(@RequestParam(required = false)String status){
        List<OrderResp> orders = orderService.getUserOrders(status);
        return ResponseEntity.ok(Outbound.ok(orders));
    }
    
    @GetMapping("/orders/{orderId}")
    @Operation(summary = "獲取訂單詳情")
    public ResponseEntity<Outbound> getOrderDetail(@PathVariable String orderId){
        OrderResp order = orderService.getOrderDetail(orderId);
        return ResponseEntity.ok(Outbound.ok(order));
    }
}
