package com.shop.ecs.controller;

import com.shop.ecs.common.result.Outbound;
import com.shop.ecs.service.CouponService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/coupon")
@Tag(name = "Coupon", description = "優惠券相關API")
public class CouponController {

  @Autowired private CouponService couponService;

  @GetMapping("/validate/{code}")
  @Operation(summary = "驗證優惠券")
  public ResponseEntity<Outbound> validateCoupon(@PathVariable String code) {
    return ResponseEntity.ok(couponService.validateCoupon(code));
  }
}
