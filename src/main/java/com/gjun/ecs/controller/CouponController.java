package com.gjun.ecs.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;

@RestController
@RequestMapping("/api/coupon")
@Tag(name = "Coupon", description = "優惠券相關API")
public class CouponController {

    @GetMapping("/validate/{code}")
    @Operation(summary = "驗證優惠券是否有效")
    public ResponseEntity<?> validateCoupon(@PathVariable String code) {
        // 暫時先寫死邏輯測試，之後可以移到 CouponService
        if ("DOUBLE11".equalsIgnoreCase(code)) {
            return ResponseEntity.ok(Map.of(
                "valid", true, 
                "discountAmount", 100, 
                "message", "成功套用優惠券！"
            ));
        }
        
        return ResponseEntity.ok(Map.of(
            "valid", false, 
            "discountAmount", 0, 
            "message", "此優惠代碼無效"
        ));
    }
}