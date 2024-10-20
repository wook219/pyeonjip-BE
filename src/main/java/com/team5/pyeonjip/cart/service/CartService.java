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
                Cart newCart = new Cart();
                newCart.setEmail(email);
                newCart.setOptionId(cartDto.getOptionId());
                newCart.setQuantity(cartDto.getQuantity());
                cartRepository.save(newCart);
                return convertToCartDto(newCart);
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
            target.setQuantity(dto.getQuantity());
            cartRepository.save(target);
            return dto;
    }

    @Transactional
    public void deleteCartItemByEmailAndOptionId(String email, Long optionId) {
        cartRepository.findByEmailAndOptionId(email, optionId);
            cartRepository.deleteByEmailAndOptionId(email, optionId);
    }

    @Transactional
    public void deleteAllCartItems(String email) {
            cartRepository.deleteAllByEmail(email);
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
                cartRepository.save(serverItem);
            } else {
                // 서버에 없는 경우 새로 추가
                Cart newCartItem = new Cart();
                newCartItem.setEmail(email);
                newCartItem.setOptionId(localItem.getOptionId());
                newCartItem.setQuantity(localItem.getQuantity());
                cartRepository.save(newCartItem);
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


