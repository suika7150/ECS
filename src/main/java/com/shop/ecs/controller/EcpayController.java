package com.shop.ecs.controller;

import com.shop.ecs.dto.response.EcpayParamsResp;
import com.shop.ecs.service.EcpayService;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payment")
public class EcpayController {

  @Autowired
  private EcpayService ecpayService;

  @PostMapping("/params/{paymentId}")
  public ResponseEntity<?> getEcpayParams(@PathVariable Long paymentId) {

    // 接收 EcpayService 回傳的 DTO 物件
    EcpayParamsResp params = ecpayService.generatePaymentParams(paymentId);

    // 回傳給前端
    return ResponseEntity.ok(params);
  }

  // 綠界付款結果回傳 (Callback)
  // 必須是 POST，且路徑要跟 application-sit.properties 裡的 return-url 對應
  @PostMapping("/callback")
  public String ecpayCallback(@RequestParam Map<String, String> formData) {

    ecpayService.handleCallback(formData);

    // 綠界規定：處理完成後必須回傳 "1|OK"
    // 如果沒回傳這串字，綠界會判定你的伺服器出錯，會一直重複發送通知
    return "1|OK";
  }
}
