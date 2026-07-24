package com.shop.ecs.service;

import com.shop.ecs.constant.OrderStatusEnum;
import com.shop.ecs.constant.PaymentStatusEnum;
import com.shop.ecs.dto.response.EcpayParamsResp;
import com.shop.ecs.entity.OrderEntity;
import com.shop.ecs.entity.PaymentEntity;
import com.shop.ecs.repository.OrderRepository;
import com.shop.ecs.repository.PaymentRepository;

import jakarta.transaction.Transactional;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EcpayService {

  private static final Logger log = LoggerFactory.getLogger(EcpayService.class);

  @Autowired
  private PaymentRepository paymentRepository;

  @Autowired
  private OrderRepository orderRepository;

  // 從 application-sit.properties 讀取設定
  @Value("${ecpay.merchant-id}")
  private String merchantId;

  @Value("${ecpay.hash-key}")
  private String hashKey;

  @Value("${ecpay.hash-iv}")
  private String hashIv;

  @Value("${ecpay.return-url}")
  private String returnUrl;

  @Value("${ecpay.client-back-url}")
  private String clientBackUrl;

  @Transactional
  public EcpayParamsResp createPayment(OrderEntity order) {

    PaymentEntity payment = new PaymentEntity();
    payment.setOrderId(order.getId()); // 關聯訂單 ID
    payment.setTotalAmount(order.getTotal()); // 同步金額
    payment.setRtnCode("0"); // 初始狀態設為 0 (代表未付款)

    PaymentEntity savedPayment = paymentRepository.save(payment);

    return generatePaymentParams(savedPayment.getId());
  }

  public EcpayParamsResp generatePaymentParams(Long paymentId) {

    // 取得資料庫中的支付紀錄
    PaymentEntity payment = paymentRepository
        .findById(paymentId)
        .orElseThrow(() -> new RuntimeException("找不到訂單: " + paymentId));

    // 產生 MerchantTradeNo，因綠界的 API 要求每一筆交易都必須帶有一個 MerchantTradeNo（特店交易編號）不能重複。
    String dateStr = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyMMdd"));
    String randomStr = UUID.randomUUID().toString().substring(0, 2).toUpperCase();
    String idStr = String.format("%06d", paymentId);
    String merchantTradeNo = "OD" + dateStr + randomStr + idStr;

    payment.setMerchantTradeNo(merchantTradeNo);
    paymentRepository.save(payment);

    // 填入基礎參數 (使用 TreeMap 自動按 A-Z 排序，加密才不會錯)
    Map<String, String> params = new TreeMap<>();
    params.put("MerchantID", merchantId);
    params.put("MerchantTradeNo", merchantTradeNo);
    params.put(
        "MerchantTradeDate",
        LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")));
    params.put("PaymentType", "aio");
    params.put("TotalAmount", String.valueOf(payment.getTotalAmount()));
    params.put("TradeDesc", "ECS_Store_Order");
    params.put("ItemName", "ECS電商平台商品一批");
    params.put("ReturnURL", returnUrl);
    params.put("ChoosePayment", "Credit");
    params.put("EncryptType", "1");
    params.put("ClientBackURL", clientBackUrl + "/" + merchantTradeNo);

    // 計算 CheckMacValue
    String checkMacValue = generateCheckMacValue(params);

    // 轉成 Resp 回傳
    EcpayParamsResp resp = new EcpayParamsResp();
    resp.setMerchantID(params.get("MerchantID"));
    resp.setMerchantTradeNo(params.get("MerchantTradeNo"));
    resp.setMerchantTradeDate(params.get("MerchantTradeDate"));
    resp.setPaymentType(params.get("PaymentType"));
    resp.setTotalAmount(params.get("TotalAmount"));
    resp.setTradeDesc(params.get("TradeDesc"));
    resp.setItemName(params.get("ItemName"));
    resp.setReturnURL(params.get("ReturnURL"));
    resp.setChoosePayment(params.get("ChoosePayment"));
    resp.setEncryptType(params.get("EncryptType"));
    resp.setClientBackURL(params.get("ClientBackURL"));
    resp.setCheckMacValue(checkMacValue);

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
        if (hex.length() == 1)
          hexString.append('0');
        hexString.append(hex);
      }
      return hexString.toString().toUpperCase();
    } catch (Exception e) {
      throw new RuntimeException("加密失敗", e);
    }
  }

  @Transactional
  public void handleCallback(Map<String, String> formData) {

    System.out.println("收到綠界 Callback: " + formData);

    String merchantTradeNo = formData.get("MerchantTradeNo");
    String rtnCode = formData.get("RtnCode");
    // String rtnMsg = formData.get("RtnMsg");
    // String paymentDate = formData.get("PaymentDate");

    PaymentEntity payment = paymentRepository.findByMerchantTradeNo(merchantTradeNo);
    if (payment == null) {
      throw new RuntimeException("找不到付款資料: " + merchantTradeNo);
    }

    boolean success = "1".equals(rtnCode);

    payment.setRtnCode(rtnCode);
    payment.setRtnMsg(formData.get("RtnMsg"));
    payment.setPaymentDate(formData.get("PaymentDate"));
    paymentRepository.save(payment);

    Long orderId = payment.getOrderId();

    log.info("PaymentEntity callback success={}, orderId={}", success, orderId);

    OrderEntity order = orderRepository.findById(orderId)
        .orElseThrow(() -> new RuntimeException("找不到訂單: " + orderId));

    if (order.getOrderStatus() == OrderStatusEnum.CANCELLED) {
      log.warn("訂單已取消，忽略付款 callback，orderId={}", orderId);
      return;
    }

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
