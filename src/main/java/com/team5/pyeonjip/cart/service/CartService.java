package com.team5.pyeonjip.cart.service;

import com.team5.pyeonjip.cart.dto.CartDetailDto;
import com.team5.pyeonjip.cart.dto.CartDto;
import com.team5.pyeonjip.cart.entity.Cart;
import com.team5.pyeonjip.cart.repository.CartRepository;
import com.team5.pyeonjip.global.exception.ErrorCode;
import com.team5.pyeonjip.global.exception.GlobalException;
import com.team5.pyeonjip.product.entity.ProductDetail;
import com.team5.pyeonjip.product.repository.ProductDetailRepository;
import com.team5.pyeonjip.product.service.ProductService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import static com.team5.pyeonjip.global.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartService {
    private final CartRepository cartRepository;
    private final ProductService productService;
    private final ProductDetailRepository productDetailRepository;

    // 조회
    public List<CartDto> getCartItemsByEmail(String email) {
        List<Cart> serverCartItems = cartRepository.findAllByEmail(email)
                .orElseThrow(() -> new GlobalException(ErrorCode.CART_NOT_FOUND));
        return serverCartItems.stream()
                .map(this::convertToCartDto)
                .collect(Collectors.toList());
    }

    public List<CartDetailDto> mapCartDtosToCartDetails(List<CartDto> cartDtos) {
        return cartDtos.stream().map(cartDto -> {
            ProductDetail productDetail = productDetailRepository.findById(cartDto.getOptionId())
                    .orElseThrow(() -> new GlobalException(PRODUCT_DETAIL_NOT_FOUND));
            CartDetailDto cartDetailDto = new CartDetailDto();
            cartDetailDto.setOptionId(productDetail.getId());
            cartDetailDto.setName(productDetail.getProduct().getName());
            cartDetailDto.setOptionName(productDetail.getName());
            cartDetailDto.setPrice(productDetail.getPrice());
            cartDetailDto.setQuantity(cartDto.getQuantity());
            cartDetailDto.setMaxQuantity(productDetail.getQuantity());
            cartDetailDto.setUrl(productDetail.getMainImage());
            return cartDetailDto;
        }).toList();
    }

    @Transactional
    public CartDto addCartDto(CartDto cartDto, String email) {
        Cart existingCart = cartRepository.findByEmailAndOptionId(email, cartDto.getOptionId());
        if (cartDto.getQuantity() == null || cartDto.getQuantity() < 0) {
            throw new GlobalException(ErrorCode.CART_ITEM_QUANTITY_INVALID);
        }
        if (existingCart != null) {
            existingCart.setQuantity(existingCart.getQuantity() + 1);
            Cart updatedCart = cartRepository.save(existingCart);
            return convertToCartDto(updatedCart);
        } else {
            try {
                Cart newCart = new Cart();
                newCart.setEmail(email);
                newCart.setOptionId(cartDto.getOptionId());
                newCart.setQuantity(cartDto.getQuantity());
                cartRepository.save(newCart);
                return convertToCartDto(newCart);
            }
            catch (Exception e) {
                throw new GlobalException(ErrorCode.CART_OPERATION_FAILED);
            }
        }
    }

    @Transactional
    public CartDto updateCartItemQuantity(String email, Long optionId, CartDto dto) {
        Cart target = cartRepository.findByEmailAndOptionId(email, optionId);
        ProductDetail productDetail = productDetailRepository.findById(optionId)
                .orElseThrow(() -> new GlobalException(PRODUCT_DETAIL_NOT_FOUND));
        if (dto.getQuantity() > productDetail.getQuantity()) {
            throw new GlobalException(OUT_OF_STOCK);
        }
        try {
            target.setQuantity(dto.getQuantity());
            cartRepository.save(target);
            return dto;
        } catch (Exception e) {
            throw new GlobalException(ErrorCode.CART_OPERATION_FAILED);
        }
    }

    @Transactional
    public void deleteCartItemByEmailAndOptionId(String email, Long optionId) {
        cartRepository.findByEmailAndOptionId(email, optionId);
        try {
            cartRepository.deleteByEmailAndOptionId(email, optionId);
        } catch (Exception e) {
            throw new GlobalException(ErrorCode.CART_OPERATION_FAILED);
        }
    }

    @Transactional
    public void deleteAllCartItems(String email) {
        try {
            cartRepository.deleteAllByEmail(email);
        } catch (Exception e) {
            throw new GlobalException(ErrorCode.CART_OPERATION_FAILED);
        }
    }

    @Transactional
    public List<CartDto> sync(String email, List<CartDto> localCartItems) {
        Map<Long, Cart> serverItemMap = cartRepository.findAllByEmail(email)
                .orElseThrow(() -> new GlobalException(CART_NOT_FOUND))
                .stream()
                .collect(Collectors.toMap(Cart::getOptionId, Function.identity()));
        // 로컬 카트 아이템을 순회하여 동기화
        for (CartDto localItem : localCartItems) {
            Cart serverItem = serverItemMap.get(localItem.getOptionId());

            if (serverItem != null) {
                // 서버에 아이템이 존재하면 수량 동기화
                serverItem.setQuantity(localItem.getQuantity());
                try {
                    cartRepository.save(serverItem);
                } catch (Exception e) {
                    throw new GlobalException(ErrorCode.CART_OPERATION_FAILED);
                }
            } else {
                // 서버에 없는 경우 새로 추가
                Cart newCartItem = new Cart();
                newCartItem.setEmail(email);
                newCartItem.setOptionId(localItem.getOptionId());
                newCartItem.setQuantity(localItem.getQuantity());
                try {
                    cartRepository.save(newCartItem);
                } catch (Exception e) {
                    throw new GlobalException(ErrorCode.CART_OPERATION_FAILED);
                }
            }
        }
        return localCartItems;
    }

    private CartDto convertToCartDto(Cart cart) {
        CartDto cartDto = new CartDto();
        cartDto.setOptionId(cart.getOptionId());
        cartDto.setQuantity(cart.getQuantity());
        return cartDto;
    }
}


