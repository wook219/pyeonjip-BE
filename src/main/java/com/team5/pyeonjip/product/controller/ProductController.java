package com.team5.pyeonjip.product.controller;

import com.team5.pyeonjip.product.dto.ProductRequest;
import com.team5.pyeonjip.product.dto.ProductResponse;
import com.team5.pyeonjip.product.entity.ProductDetail;
import com.team5.pyeonjip.product.entity.ProductImage;
import com.team5.pyeonjip.product.service.ProductDetailService;
import com.team5.pyeonjip.product.service.ProductImageService;
import com.team5.pyeonjip.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ProductDetailService productDetailService;
    private final ProductImageService productImageService;

    // CategoryId로 제품 목록 조회
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductResponse>> getProductsByCategory(@PathVariable("categoryId") Long categoryId) {
        List<ProductResponse> products = productService.getProductsByCategoryId(categoryId);
        return ResponseEntity.ok(products);
    }

    // ProductId로 단일 상품 조회
    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable("productId") Long productId) {
        ProductResponse productResponse = productService.getProductById(productId);
        return ResponseEntity.ok(productResponse);
    }

    // 모든 상품 조회 (컨트롤러)
    @GetMapping("/all")
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        List<ProductResponse> productResponse = productService.getAllProducts();
        return ResponseEntity.ok(productResponse);
    }

    // 여러 CategoryId 로 제품 목록 조회
    @GetMapping("/categories")
    public ResponseEntity<List<ProductResponse>> getProductsByMultipleCategoryIds(
            @RequestParam List<Long> categoryIds) {
        return ResponseEntity.ok(productService.getProductsByMultipleCategoryIds(categoryIds));
    }

    // ProductId로 옵션 목록 조회
    @GetMapping("/{productId}/details")
    public ResponseEntity<List<ProductDetail>> getProductDetails(@PathVariable("productId") Long productId) {
        List<ProductDetail> productDetails = productDetailService.getProductDetailsByProductId(productId);
        return ResponseEntity.ok(productDetails);
    }

    // 특정 옵션(디테일) 조회
    @GetMapping("/details/{detailId}")
    public ResponseEntity<ProductDetail> getProductDetailById(@PathVariable("detailId") Long detailId) {
        ProductDetail productDetail = productDetailService.getProductDetailById(detailId);
        return ResponseEntity.ok(productDetail);

    }

    // 컨트롤러 페이지 네이션
    @GetMapping("/all-pages")
    public ResponseEntity<Page<ProductResponse>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size) {
        Page<ProductResponse> productResponse = productService.getAllProductspage(PageRequest.of(page, size));
        return ResponseEntity.ok(productResponse);
    }

    @GetMapping("/{productId}/images")
    public ResponseEntity<List<ProductImage>> getProductImages(@PathVariable("productId") Long productId) {
        List<ProductImage> productImages = productService.getProductImagesByProductId(productId);
        return ResponseEntity.ok(productImages);
    }
}