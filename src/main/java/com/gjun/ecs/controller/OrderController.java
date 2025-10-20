package com.gjun.ecs.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gjun.ecs.dto.request.OrderReq;
import com.gjun.ecs.dto.response.Outbound;
import com.gjun.ecs.service.OrderService;


@RestController
@RequestMapping("/orders")
public class OrderController {
    
    @Autowired
    private OrderService orderService;
    
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Outbound> createOrder(
        @RequestBody OrderReq orderReq){
        Outbound resp = Outbound.ok(orderService.createOrder(orderReq));
        return ResponseEntity.ok(resp);
        
    }
}
