package com.shop.ecs.controller;

import com.shop.ecs.common.result.Outbound;
import com.shop.ecs.service.CouponService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/coupons")
@Tag(name = "優惠券管理 API", description = "優惠券驗證")
public class CouponController {

  @Autowired
  private CouponService couponService;

  @GetMapping("/validate/{couponCode}")
  @Operation(summary = "驗證優惠券")
  public ResponseEntity<Outbound> validateCoupon(@PathVariable String couponCode) {
    return ResponseEntity.ok(couponService.validateCoupon(couponCode));
  }
}
