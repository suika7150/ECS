package com.gjun.ecs.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gjun.ecs.dto.request.OrderReq;
import com.gjun.ecs.dto.response.OrderResp;
import com.gjun.ecs.entity.Order;
import com.gjun.ecs.entity.Product;
import com.gjun.ecs.repository.OrdersRepository;
import com.gjun.ecs.repository.ProductRepository;

import jakarta.transaction.Transactional;

/**
    購物車結帳訂單
 **/

@Service
public class OrderService{

    @Autowired
    private OrdersRepository ordersRepository;
    @Autowired
    private ProductRepository productRepository;
    
    @Transactional
    public OrderResp createOrder(OrderReq req) {

        for(OrderReq.Item item : req.getItems()) {
            Product product = productRepository.findByIdForUpdate(item.getProductId());
            if(product == null) {
                throw new RuntimeException("商品不存在" + item.getProductId());
            }
            if(product.getStock() < item.getQuantity()) {
                throw new RuntimeException("商品庫存不足: " + product.getName());
            }
            product.setStock(product.getStock() - item.getQuantity());
            productRepository.save(product);
        }

        Order orders = Order.builder()
            .name(req.getName())
            .phone(req.getPhone())
            .address(req.getAddress())
            .shippingMethod(req.getShippingMethod())
            .notes(req.getNotes())
            .paymentMethod(req.getPaymentMethod())
            .total(req.getTotal())
            .cardLast4(req.getCardLast4())
            .paymentStatus(req.getPaymentStatus())
            .build();
            
            Order newOrder = ordersRepository.save(orders);
            
            return OrderResp.builder()
                .id(newOrder.getId())
                .name(newOrder.getName())
                .phone(newOrder.getPhone())
                .address(newOrder.getAddress())
                .shippingMethod(newOrder.getShippingMethod())
                .notes(newOrder.getNotes())
                .paymentMethod(newOrder.getPaymentMethod())
                .total(newOrder.getTotal())
                .cardLast4(req.getCardLast4())
                .paymentStatus(req.getPaymentStatus())
                .build();

    }
}