package com.gjun.ecs.service;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.gjun.ecs.dto.response.EcpayParamsResp;
import com.gjun.ecs.entity.Order;
import com.gjun.ecs.entity.Payment;
import com.gjun.ecs.repository.PaymentRepository;

@Service
public class EcpayService {

    @Autowired
    private PaymentRepository paymentRepository;

    // 從 application-sit.properties 讀取設定
    @Value("${ecpay.merchant-id}")
    private String merchantId;

    @Value("${ecpay.hash-key}")
    private String hashKey;

    @Value("${ecpay.hash-iv}")
    private String hashIv;

    @Value("${ecpay.return-url}")
    private String returnUrl;

    /**
    * 這是給 OrderService 呼叫的入口
    * 負責建立 Payment 紀錄並產生綠界參數
    */
    @org.springframework.transaction.annotation.Transactional
    public EcpayParamsResp createPayment(Order order) {
    
    // 1. 建立一筆新的 Payment 紀錄（這張表是你早上建的）
    Payment payment = new Payment();
    payment.setOrderId(order.getId());         // 關聯訂單 ID
    payment.setTotalAmount(order.getTotal());  // 同步金額
    payment.setRtnCode("0");           // 初始狀態設為 0 (代表未付款)
    
    // 2. 存入資料庫以取得 paymentId
    Payment savedPayment = paymentRepository.save(payment);
    
    // 3. 呼叫你原本就寫好的參數產生邏輯
    // 這樣就能重複利用你寫好的加密 (CheckMacValue) 邏輯
    return generatePaymentParams(savedPayment.getId());
}

    public EcpayParamsResp generatePaymentParams(Long paymentId) {
        
        // 取得資料庫中的支付紀錄
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("找不到訂單: " + paymentId));

        // 綠界的 API 要求每一筆交易都必須帶有一個 MerchantTradeNo（特店交易編號）不能重複。
        String merchantTradeNo = "ECS" + paymentId + UUID.randomUUID().toString().substring(0, 5);

        payment.setMerchantTradeNo(merchantTradeNo);
        paymentRepository.save(payment);
        
        // 填入基礎參數 (使用 TreeMap 自動按 A-Z 排序，加密才不會錯)
        Map<String, String> params = new TreeMap<>();
        params.put("MerchantID", merchantId);
        params.put("MerchantTradeNo", merchantTradeNo);
        params.put("MerchantTradeDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")));
        params.put("PaymentType", "aio");
        params.put("TotalAmount", String.valueOf(payment.getTotalAmount()));
        params.put("TradeDesc", "ECS_Store_Order");
        params.put("ItemName", "ECS商品一批");
        params.put("ReturnURL", returnUrl);
        params.put("ChoosePayment", "Credit");
        params.put("EncryptType", "1");

        // 4. 計算 CheckMacValue
        String checkMacValue = generateCheckMacValue(params);

        // 5. 封裝進 DTO 回傳
        EcpayParamsResp resp = new EcpayParamsResp();
        resp.setMerchantID(merchantId);
        resp.setMerchantTradeNo(merchantTradeNo);
        resp.setCheckMacValue(checkMacValue);
        resp.setTradeAmt(payment.getTotalAmount()); // 金額
        resp.setRtnCode(payment.getRtnCode());
        // 如果你的 DTO 還有其他欄位，記得在這裡補上 set
        
        // 初始狀態 (前端需要知道現在是未付款)
        resp.setRtnCode(payment.getRtnCode());

        return resp;
    }

    // 綠界專用加密演算法
    private String generateCheckMacValue(Map<String, String> params) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("HashKey=").append(hashKey);
            for (Map.Entry<String, String> entry : params.entrySet()) {
                sb.append("&").append(entry.getKey()).append("=").append(entry.getValue());
            }
            sb.append("&HashIV=").append(hashIv);

            String encoded = URLEncoder.encode(sb.toString(), StandardCharsets.UTF_8.toString())
                    .toLowerCase()
                    .replace("%2d", "-")
                    .replace("%5f", "_")
                    .replace("%2e", ".")
                    .replace("%2a", "*")
                    .replace("%28", "(")
                    .replace("%29", ")")
                    .replace("%21", "!");

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(encoded.getBytes(StandardCharsets.UTF_8));
            
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString().toUpperCase();
        } catch (Exception e) {
            throw new RuntimeException("加密失敗", e);
        }
    }
}