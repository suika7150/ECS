package com.gjun.ecs.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gjun.ecs.dto.request.OrderReq;
import com.gjun.ecs.dto.response.EcpayParamsResp;
import com.gjun.ecs.dto.response.OrderItemResp;
import com.gjun.ecs.dto.response.OrderResp;
import com.gjun.ecs.entity.Order;
import com.gjun.ecs.entity.OrderItem;
import com.gjun.ecs.entity.Product;
import com.gjun.ecs.enums.OrderStatus;
import com.gjun.ecs.repository.OrderRepository;
import com.gjun.ecs.repository.ProductRepository;
import com.gjun.ecs.utils.ImageUtils;

import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
    購物車結帳訂單
 **/

@Service
public class OrderService{

    @Autowired
    private OrderRepository orderRepository;
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
            .paymentStatus(OrderStatus.PENDING.name().toLowerCase())
            .build();
            
            List<OrderItem> itemList = new ArrayList<>();

            for (OrderReq.Item itemReq : req.getItems()) {
                Product product = productRepository.findById(itemReq.getProductId()).orElse(null);
    
                OrderItem orderItem = OrderItem.builder()
                    .order(orders) 
                    .productId(itemReq.getProductId())
                    .productName(product != null ? product.getName() : "未知商品")
                    .price(product != null ? product.getPrice() : 0)
                    .quantity(itemReq.getQuantity())
                    .productImage(product != null ? ImageUtils.toBase64Src(product.getImageData(), product.getImageType()) : "")
                    .build();
    
                itemList.add(orderItem);
            } 

        
            // 存 Order 的同時會自動存 OrderItem
            orders.setItems(itemList);

            Order newOrder = orderRepository.save(orders);
            
            log.info("訂單建立成功: ID={}，姓名={}", newOrder.getId(), newOrder.getName());

            // 呼叫 PaymentService 產生綠界參數
            EcpayParamsResp ecpayParams = ecpayService.createPayment(newOrder);

            // 將編號存回 newOrder 並再次存檔
            newOrder.setMerchantTradeNo(ecpayParams.getMerchantTradeNo());
            orderRepository.save(newOrder);

            // 將綠界參數回傳
            OrderResp resp = convertToOrderResp(newOrder);
            resp.setEcpayParams(ecpayParams);
            return resp;
        

    }

        /**
        * 獲取使用者訂單列表
        * @param status 篩選狀態 (UNPAID, PAID, SHIPPED, COMPLETED 等)
        */
        public List<OrderResp> getUserOrders(String status){
            log.info("📢 收到訂單篩選請求，原始狀態參數: [{}]", status);
            List<Order> orders;
        
            // 判斷是否需要篩選狀態
            if(status == null || status.trim().isEmpty() || status.equalsIgnoreCase("all")){
                // 如果 status 是 null、空字串或 "all"，則切換為查詢所有訂單
                orders = orderRepository.findAllByOrderByIdDesc();
            } else {
                String normalizedStatus = status.toLowerCase();
                log.info("🔍 執行條件查詢，標準化狀態碼: [{}]", normalizedStatus);

                orders = orderRepository.findByPaymentStatusOrderByIdDesc(normalizedStatus);
            }

            log.info("✅ 查詢完成，結果筆數: {}", orders.size());

            return orders.stream()
                .map(this::convertToOrderResp)
                    .toList();
                        
    }

        /**
        * 獲取單筆訂單詳情
        */
       public OrderResp getOrderDetail(String orderId){
       
            Order order = orderRepository.findById(Long.parseLong(orderId))
            .orElseThrow(() -> new RuntimeException("找不到該筆訂單"));


            return convertToOrderResp(order);
    }

    private OrderResp convertToOrderResp(Order order){

        OrderResp resp = OrderResp.builder()
            .id(order.getId())
            .name(order.getName())
            .phone(order.getPhone())
            .address(order.getAddress())
            .shippingMethod(order.getShippingMethod())
            .notes(order.getNotes())
            .paymentMethod(order.getPaymentMethod())
            .createdAt(order.getCreatedAt())
            .discount(order.getDiscount())
            .total(order.getTotal())
            .paymentStatus(order.getPaymentStatus())
            .merchantTradeNo(order.getMerchantTradeNo()) 
            .build();

            if(order.getItems() != null){

                List<OrderItemResp> itemResps = order.getItems().stream()
                    .map(item -> OrderItemResp.builder()
                        .name(item.getProductName())             
                        .price(item.getPrice())
                        .quantity(item.getQuantity())
                        .productImage(item.getProductImage()) 
                        .build())
                    .toList();
                        
                resp.setItems(itemResps);
        }

        return resp;
    
    }
}