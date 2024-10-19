package com.team5.pyeonjip.coupon.service;

import com.team5.pyeonjip.coupon.entity.Coupon;
import com.team5.pyeonjip.coupon.repository.CouponRepository;
import com.team5.pyeonjip.global.exception.ErrorCode;
import com.team5.pyeonjip.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class CouponService {
    private final CouponRepository couponRepository;

    // 랜덤 쿠폰 생성
    @Transactional
    public Coupon createRandomCoupon(Long discount){
        Coupon coupon = new Coupon();
        coupon.setCode(generateCouponCode()); // 코드 자동 생성
        coupon.setDiscount(discount);
        coupon.setActive(true);
        coupon.setExpiryDate(LocalDateTime.now().plusDays(7)); // 7일간의 유효기간을 가짐

        validateDiscount(discount);
        validateCouponCode(coupon.getCode());
        return saveCoupon(coupon);
    }
    // 사용자 지정 쿠폰 생성
    @Transactional
    public Coupon createCoupon(String Code, Long discount, LocalDateTime expiryDate){
        Coupon coupon = new Coupon();
        coupon.setCode(Code);
        coupon.setDiscount(discount);
        coupon.setActive(true);
        coupon.setExpiryDate(expiryDate);

        validateDiscount(discount);
        validateCouponCode(coupon.getCode());
        return saveCoupon(coupon);
    }

    // 쿠폰 활성화/비활성화
    @Transactional
    public void useCoupon(Long id) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new GlobalException(ErrorCode.COUPON_NOT_FOUND));
        coupon.setActive(false);
        couponRepository.save(coupon);
    }

    public Coupon saveCoupon(Coupon coupon) {
        return couponRepository.save(coupon);
    }

    public List<Coupon> getAllCoupons() {
        return couponRepository.findAll();
    }

    public void deleteCouponById(Long id) {
        if(!couponRepository.existsById(id)) {
            throw new GlobalException(ErrorCode.COUPON_NOT_FOUND);
        }
        couponRepository.deleteById(id);
    }

    // 쿠폰 코드 자동 생성
    public String generateCouponCode() {
        return UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }

    private void validateDiscount(Long discount) {
        if(discount < 0 || discount > 100 ){
            throw new GlobalException(ErrorCode.INVALID_COUPON_DISCOUNT);
        }
    }

    private void validateCouponCode(String code) {
        if(couponRepository.existsByCode(code)){
            throw new GlobalException(ErrorCode.INVALID_COUPON_CODE);
        }
    }
}
