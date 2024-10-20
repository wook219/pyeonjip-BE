package com.team5.pyeonjip.coupon.controller;

import com.team5.pyeonjip.coupon.entity.Coupon;
import com.team5.pyeonjip.coupon.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/coupon")
@RequiredArgsConstructor
public class CouponController {
    private final CouponService couponService;

    // 랜덤 쿠폰 생성 API
    @PostMapping
    public ResponseEntity<Coupon> createCoupon(@RequestParam Long discount) {
        Coupon coupon = couponService.createRandomCoupon(discount);
        return ResponseEntity.status(HttpStatus.CREATED).body(coupon);
    }

    // 커스텀 쿠폰 생성 API
    @PostMapping("/custom")
    public ResponseEntity<Coupon> createCouponCustom(
            @RequestParam String code,
            @RequestParam Long discount,
            @RequestParam LocalDateTime expiryDate) {
        Coupon coupon = couponService.createCoupon(code, discount, expiryDate);
        return ResponseEntity.status(HttpStatus.CREATED).body(coupon);
    }

    // 쿠폰 목록 조회
    @GetMapping
    public ResponseEntity<List<Coupon>> getCoupons() {
        List<Coupon> coupons = couponService.getAllCoupons();
        return ResponseEntity.status(HttpStatus.OK).body(coupons);
    }

    // 쿠폰 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCoupon(@PathVariable Long id) {
        couponService.deleteCouponById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // 쿠폰 사용 (상태 관리)
    @PostMapping("/use/{id}")
    public ResponseEntity<Void> useCoupon(@PathVariable Long id){
        couponService.useCoupon(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}


