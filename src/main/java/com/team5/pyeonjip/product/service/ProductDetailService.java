package com.team5.pyeonjip.product.service;

import com.team5.pyeonjip.cart.repository.CartRepository;
import com.team5.pyeonjip.global.exception.ErrorCode;
import com.team5.pyeonjip.global.exception.GlobalException;
import com.team5.pyeonjip.product.dto.ProductRequest;
import com.team5.pyeonjip.product.entity.Product;
import com.team5.pyeonjip.product.entity.ProductDetail;
import com.team5.pyeonjip.product.repository.ProductDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductDetailService {
    private final ProductDetailRepository productDetailRepository;
    private final CartRepository cartRepository;

    // Create - 옵션 생성
    @Transactional
    public void createProductDetails(Product product, List<ProductRequest.ProductDetailRequest> detailRequests) {
        List<ProductDetail> productDetails = detailRequests.stream()
                .map(detailRequest -> new ProductDetail(product, detailRequest.getName(), detailRequest.getPrice(), detailRequest.getQuantity()))
                .collect(Collectors.toList());
        productDetailRepository.saveAll(productDetails);
    }

    // Read - 상품의 모든 옵션 조회
    public List<ProductDetail> getProductDetailsByProduct(Product product) {
        return productDetailRepository.findByProductId(product.getId());
    }

    // Update - 옵션 업데이트
    @Transactional
    public void updateProductDetails(Product product, List<ProductRequest.ProductDetailRequest> detailRequests) {
        List<ProductDetail> existingDetails = productDetailRepository.findByProductId(product.getId());

        existingDetails.forEach(existing -> {
            detailRequests.stream()
                    .filter(request -> request.getName().equals(existing.getName()))
                    .findFirst()
                    .ifPresent(request -> {
                        existing.setPrice(request.getPrice());
                        existing.setQuantity(request.getQuantity());
                    });
        });

        productDetailRepository.saveAll(existingDetails);
    }

    // Delete - 옵션 삭제 및 연관된 CartItem, OrderItem 삭제
    @Transactional
    public void deleteProductDetailsByProduct(Product product) {
        List<ProductDetail> existingDetails = productDetailRepository.findByProductId(product.getId());

        // CartItem 및 OrderItem 삭제 로직 추가
        for (ProductDetail detail : existingDetails) {
            cartRepository.deleteByOptionId(detail.getId());  // 해당 ProductDetail과 연관된 CartItem 삭제

        }

        // ProductDetail 삭제
        productDetailRepository.deleteAll(existingDetails);
    }

    // Quantity Update - 수량 조절 메서드 추가
    @Transactional
    public void updateDetailQuantity(Long detailId, Long quantity) {
        ProductDetail productDetail = productDetailRepository.findById(detailId)
                .orElseThrow(() -> new GlobalException(ErrorCode.PRODUCT_DETAIL_NOT_FOUND));
        productDetail.setQuantity(productDetail.getQuantity() + quantity); // 수량 변경
        productDetailRepository.save(productDetail);
    }

    // 단일 ProductDetail 생성
    @Transactional
    public ProductDetail createProductDetail(Long productId, ProductDetail productDetail) {
        productDetail.setProduct(new Product(productId));  // Product와 연결
        return productDetailRepository.save(productDetail);
    }

    // 단일 ProductDetail 삭제
    @Transactional
    public void deleteProductDetail(Long detailId) {
        ProductDetail productDetail = productDetailRepository.findById(detailId)
                .orElseThrow(() -> new GlobalException(ErrorCode.PRODUCT_DETAIL_NOT_FOUND));

        cartRepository.deleteByOptionId(detailId);  // 해당 ProductDetail과 연관된 CartItem 삭제

        productDetailRepository.delete(productDetail);
    }

    // 단일 ProductDetail 수정
    @Transactional
    public ProductDetail updateProductDetail(Long detailId, ProductDetail updatedDetail) {
        ProductDetail existingDetail = productDetailRepository.findById(detailId)
                .orElseThrow(() -> new GlobalException(ErrorCode.PRODUCT_DETAIL_NOT_FOUND));

        existingDetail.setName(updatedDetail.getName());
        existingDetail.setPrice(updatedDetail.getPrice());
        existingDetail.setQuantity(updatedDetail.getQuantity());
        existingDetail.setMainImage(updatedDetail.getMainImage());

        return productDetailRepository.save(existingDetail);
    }

    // ProductId로 옵션 목록 조회
    public List<ProductDetail> getProductDetailsByProductId(Long productId) {
        return productDetailRepository.findByProductId(productId);
    }

    public ProductDetail getProductDetailById(Long detailId) {
        return productDetailRepository.findById(detailId)
                .orElseThrow(() -> new GlobalException(ErrorCode.PRODUCT_DETAIL_NOT_FOUND));
    }
}