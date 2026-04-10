package com.gjun.ecs.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gjun.ecs.dto.request.OrderReq;
import com.gjun.ecs.dto.response.EcpayParamsResp;
import com.gjun.ecs.dto.response.OrderResp;
import com.gjun.ecs.entity.Order;
import com.gjun.ecs.entity.Product;
import com.gjun.ecs.repository.OrdersRepository;
import com.gjun.ecs.repository.ProductRepository;

import jakarta.transaction.Transactional;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
    購物車結帳訂單
 **/

@Service
public class OrderService{

    @Autowired
    private OrdersRepository ordersRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private EcpayService ecpayService;

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    @Transactional
    public OrderResp createOrder(OrderReq req) {

        log.info("建立訂單請求 : {}",req);

        // 檢查商品庫存並更新庫存
        for(OrderReq.Item item : req.getItems()) {
            Product product = productRepository.findByIdForUpdate(item.getProductId());

            if(product == null) {
                log.warn("商品不存在 ID: {}", item.getProductId());
                throw new RuntimeException("商品不存在" + item.getProductId());
            }

            log.info("🛒 商品名稱={} | ID={} | 原庫存={} | 訂購數量={}",
                 product.getName(), product.getId(), product.getStock(), item.getQuantity());

            if(product.getStock() < item.getQuantity()) {
             log.warn("❌ 庫存不足：{} (剩餘 {}，訂購 {})",
                     product.getName(), product.getStock(), item.getQuantity());
                throw new RuntimeException("商品庫存不足: " + product.getName());
            }
            product.setStock(product.getStock() - item.getQuantity());
            productRepository.save(product);
        
            log.info("✅ 庫存更新後：{} (剩餘 {}，訂購 {})",
                 product.getName(), product.getStock(), item.getQuantity());

        }

        Order orders = Order.builder()
            .name(req.getName())
            .phone(req.getPhone())
            .address(req.getAddress())
            .shippingMethod(req.getShippingMethod())
            .notes(req.getNotes())
            .paymentMethod(req.getPaymentMethod())
            .couponCode(req.getCouponCode())
            .discount(req.getDiscount() == null ? 0 : req.getDiscount())
            .total(req.getTotal())
            .cardLast4(req.getCardLast4())
            .paymentStatus(("pending"))
            .build();
            
            Order newOrder = ordersRepository.save(orders);
            
            log.info("訂單建立成功: ID={}，姓名={}", newOrder.getId(), newOrder.getName());

            // 呼叫 PaymentService 產生綠界參數
            EcpayParamsResp ecpayParams = ecpayService.createPayment(newOrder);

            // 將編號存回 newOrder 並再次存檔
            newOrder.setMerchantTradeNo(ecpayParams.getMerchantTradeNo());
            ordersRepository.save(newOrder);

            // 將綠界參數回傳
            OrderResp resp = convertToOrderResp(newOrder);
            resp.setEcpayParams(ecpayParams);
            return resp;
        

    }

        /**
        * 獲取使用者訂單列表
        */
        public List<OrderResp> getUserOrders(){
            List<Order> orders = ordersRepository.findAllByOrderByIdDesc();
        
            return orders.stream()
                .map(this::convertToOrderResp)
                    .toList();
                        
    }

        /**
        * 獲取單筆訂單詳情
        */
       public OrderResp getOrderDetail(String orderId){
       
            Order order = ordersRepository.findById(Long.parseLong(orderId))
            .orElseThrow(() -> new RuntimeException("找不到該筆訂單"));


            return convertToOrderResp(order);
    }

    private OrderResp convertToOrderResp(Order order){

        return OrderResp.builder()
            .id(order.getId())
            .name(order.getName())
            .phone(order.getPhone())
            .address(order.getAddress())
            .shippingMethod(order.getShippingMethod())
            .notes(order.getNotes())
            .paymentMethod(order.getPaymentMethod())
            .discount(order.getDiscount())
            .total(order.getTotal())
            .paymentStatus(order.getPaymentStatus())
            // 這裡要注意：如果不是剛下單，merchantTradeNo 可能要從資料庫欄位拿
            // 建議你的 Order Entity 也要存下 merchantTradeNo
            .merchantTradeNo(order.getMerchantTradeNo()) 
            .build();

    }
}