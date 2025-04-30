package com.ecommerce.cartorderservice.service;

import com.ecommerce.cartorderservice.domain.Cart;
import com.ecommerce.cartorderservice.dto.CartDto;

public interface CartService {
    Cart addToCart(CartDto cartDto);
    Cart removeFromCart(String email, String productId);
    Cart getCartByEmail(String email);
    void removeCartFromRedis(String email);
}
