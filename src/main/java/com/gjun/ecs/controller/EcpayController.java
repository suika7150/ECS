package com.gjun.ecs.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gjun.ecs.dto.response.EcpayParamsResp;
import com.gjun.ecs.service.EcpayService;
@RestController
@RequestMapping("/api/payment")
public class EcpayController {
    
    @Autowired
    private EcpayService ecpayService;

    @PostMapping("/params/{paymentId}")
    public ResponseEntity<?> getEcpayParams(@PathVariable Long paymentId){
        
        // 接收 EcpayService 回傳的 DTO 物件
        EcpayParamsResp params = ecpayService.generatePaymentParams(paymentId);
        
        // 回傳給前端
        return ResponseEntity.ok(params);
    }

    // 綠界付款結果回傳 (Callback)
    // 必須是 POST，且路徑要跟 application-sit.properties 裡的 return-url 對應
    @PostMapping("/callback")
    public String ecpayCallback(@RequestParam Map<String, String> formData) {
        
        // 1. 紀錄綠界回傳的原始資料 (開發時建議印出來看)
        System.out.println(">>> 收到綠界回傳通知: " + formData);

        // 2. 取得關鍵參數
        // RtnCode: 1 為付款成功，其餘為失敗
        String rtnCode = formData.get("RtnCode"); 
        // MerchantTradeNo: 我們產生的那串 ECS...
        String merchantTradeNo = formData.get("MerchantTradeNo"); 
        
        if ("1".equals(rtnCode)) {
            // TODO: 在這裡呼叫 Service 邏輯更新資料庫狀態
            // 例如: paymentService.completePayment(merchantTradeNo);
            System.out.println("訂單 [" + merchantTradeNo + "] 付款成功！");
        } else {
            System.out.println("訂單 [" + merchantTradeNo + "] 付款失敗，代碼：" + rtnCode);
        }

        // 3. 綠界規定：處理完成後必須回傳 "1|OK" 
        // 如果沒回傳這串字，綠界會判定你的伺服器出錯，會一直重複發送通知
        return "1|OK";
    }
}
