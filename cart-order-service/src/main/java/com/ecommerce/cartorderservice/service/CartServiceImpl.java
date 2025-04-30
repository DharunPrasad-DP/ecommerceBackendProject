package com.ecommerce.cartorderservice.service;

import com.ecommerce.cartorderservice.domain.Cart;
import com.ecommerce.cartorderservice.domain.CartItem;
import com.ecommerce.cartorderservice.dto.CartDto;
import com.ecommerce.cartorderservice.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class CartServiceImpl implements CartService{

    @Autowired
    private RedisTemplate<String, Cart> redisTemplate;

    @Autowired
    private CartRepository cartRepository;

    private static final String CART_KEY_PREFIX = "cart:";

    @Override
    public Cart addToCart(CartDto cartRequest) {
        String email = cartRequest.getEmail();
        Cart cart = getCartFromRedis(email); // Check if the cart exists in Redis

        if (cart == null) {
            cart = cartRepository.findByEmail(email); // Check MongoDB if not found in Redis
            if (cart == null) {
                cart = new Cart();
                cart.setEmail(email);
                cart.setCartItems(new ArrayList<>());
            }
        }

        // Update cart items
        Optional<CartItem> existingItem = cart.getCartItems().stream()
                .filter(item -> item.getProductId().equals(cartRequest.getProductId()))
                .findFirst();

        if (existingItem.isPresent()) {
            // Update quantity of existing item
            existingItem.get().setQuantity(existingItem.get().getQuantity() + cartRequest.getQuantity());
        } else {
            // Add new item if it doesn't exist
            CartItem cartItem = new CartItem();
            cartItem.setProductId(cartRequest.getProductId());
            cartItem.setQuantity(cartRequest.getQuantity());
            cartItem.setPrice(cartRequest.getPrice());
            cart.getCartItems().add(cartItem);
        }

        // Save to MongoDB
        cartRepository.save(cart);

        // Save to Redis after modifying the cart
        saveCartToRedis(cart);

        return cart;
    }

    @Override
    public Cart removeFromCart(String email, String productId) {
        // Retrieve the cart from Redis or MongoDB
        Cart cart = getCartByEmail(email);
        if (cart == null) {
            throw new RuntimeException("Cart not found for email: " + email);
        }

        // Remove the product from the cart
        cart.getCartItems().removeIf(item -> item.getProductId().equals(productId));

        // If the cart is empty, remove it from Redis and MongoDB
        if (cart.getCartItems().isEmpty()) {
            removeCartFromRedis(email);
            cartRepository.deleteByEmail(email);
            return null;
        }

        // Save the updated cart to Redis and MongoDB
        saveCartToRedis(cart);
        cartRepository.save(cart);

        return cart;
    }

    @Override
    public Cart getCartByEmail(String email) {
        // Check Redis for the cart
        Cart cart = getCartFromRedis(email);
        if (cart != null) {
            return cart;
        }

        // If not found in Redis, check MongoDB
        cart = cartRepository.findByEmail(email);
        if (cart != null) {
            // Save the cart to Redis for future requests
            saveCartToRedis(cart);
        }

        return cart;
    }

    private Cart getCartFromRedis(String email) {
        ValueOperations<String, Cart> valueOperations = redisTemplate.opsForValue();
        return valueOperations.get(CART_KEY_PREFIX + email);
    }

    private void saveCartToRedis(Cart cart) {
        ValueOperations<String, Cart> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(CART_KEY_PREFIX + cart.getEmail(), cart);
        //TTL
        redisTemplate.expire(CART_KEY_PREFIX + cart.getEmail(), 15, TimeUnit.MINUTES);
    }

    @Override
    public void removeCartFromRedis(String email) {
        redisTemplate.delete(CART_KEY_PREFIX + email);
    }
}
