package com.gjun.ecs.service;

import com.gjun.ecs.dto.response.Outbound;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class CouponService {

    public Outbound validateCoupon(String code) {
        
        if (code == null || code.trim().isEmpty()) {
            return Outbound.builder()
                .code("9999")
                .msg("請輸入優惠代碼")
                .result(Map.of("valid", false))
                .build();
        }

        String targetCode = code.trim();

        if ("DOUBLE11".equalsIgnoreCase(targetCode)) {
            return Outbound.builder()
                .code("0000")
                .msg("成功套用優惠券！")
                .result(Map.of(
                    "valid", true,
                    "discountAmount", 100
                ))
                .build();
        }

        return Outbound.builder()
                .code("0000")
                .msg("此優惠代碼無效")
                .result(Map.of(
                    "valid", false,
                    "discountAmount", 0
                ))
                .build();
    }
}