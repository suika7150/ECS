package com.gjun.ecs.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.gjun.ecs.dto.response.Outbound;
import com.gjun.ecs.service.CouponService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/coupon")
@Tag(name = "Coupon", description = "優惠券相關API")
public class CouponController {

    @Autowired
    private CouponService couponService;

    @GetMapping("/validate/{code}")
    @Operation(summary = "驗證優惠券")
    public ResponseEntity<Outbound> validateCoupon(@PathVariable String code) {
        return ResponseEntity.ok(couponService.validateCoupon(code));
    }
}