import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EcpayService {

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

    public Map<String, String> generatePaymentParams(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("找不到訂單: " + orderId));

        Map<String, String> params = new HashMap<>();
        
        // 填入基礎參數
        params.put("MerchantID", merchantId);
        params.put("MerchantTradeNo", "ECS" + orderId + UUID.randomUUID().toString().substring(0, 5));
        params.put("MerchantTradeDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")));
        params.put("PaymentType", "aio");
        params.put("TotalAmount", String.valueOf(order.getTotalAmount()));
        params.put("TradeDesc", "ECS_Store_Order");
        params.put("ItemName", "ECS商品一批");
        params.put("ReturnURL", returnUrl);
        params.put("ChoosePayment", "Credit");
        params.put("EncryptType", "1"); 

        
        
        params.put("CheckMacValue", "計算出來的加密字串");

        return params;
    }
}