package com.shop.ecs.controller;

import com.shop.ecs.service.EcpayService;

import io.swagger.v3.oas.annotations.Operation;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payments")
public class EcpayController {

  @Autowired
  private EcpayService ecpayService;

  @PostMapping("/ecpay-params/{paymentId}")
  @Operation(summary = "取得綠界付款參數")
  public ResponseEntity<?> getEcpayParams(@PathVariable Long paymentId) {
    return ResponseEntity.ok(ecpayService.generatePaymentParams(paymentId));
  }

  // 綠界付款結果回傳 (Callback)
  // 必須是 POST，且路徑要跟 application-sit.properties 裡的 return-url 對應
  @PostMapping("/ecpay/callback")
  @Operation(summary = "綠界付款結果回傳 (Callback)")
  public String ecpayCallback(@RequestParam Map<String, String> formData) {

    ecpayService.handleCallback(formData);

    // 綠界規定：處理完成後必須回傳 "1|OK"
    // 若回傳其他內容，綠界會判定伺服器出錯，會一直重複發送通知
    return "1|OK";
  }
}
