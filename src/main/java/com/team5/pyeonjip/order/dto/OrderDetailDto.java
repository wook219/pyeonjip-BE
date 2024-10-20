package com.team5.pyeonjip.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 주문 상세 요청
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDetailDto {
    private Long productDetailId;
    private String productName; // 상품 명
    private Long quantity; // 수량
    private Long productPrice; // 상품 1개 가격
    private Long subTotalPrice; // 상품 1개 가격 * 수량
    private String productImage; // 상품 이미지
    private String productDetailName; // 상품 디테일 명
}
