package com.gjun.ecs.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gjun.ecs.service.EcpayService;
@RestController
@RequestMapping("/api/payment")
public class EcpayController {
    
    @Autowired
    private EcpayService ecpayService;

    @PostMapping("/params/{orderId}")
    public ResponseEntity<?> getEcpayParams(@PathVariable Long orderId){
        Map<String, String> params = ecpayService.generatePaymentParams(orderId);
        return ResponseEntity.ok(params);
    }
}
