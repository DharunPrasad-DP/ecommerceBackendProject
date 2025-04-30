package com.ecommerce.cartorderservice.controller;

import com.ecommerce.cartorderservice.domain.Cart;
import com.ecommerce.cartorderservice.dto.CartDto;
import com.ecommerce.cartorderservice.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${cartService.api.base.path}")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("/addToCart")
    public ResponseEntity<Cart> addToCart(@RequestBody CartDto cartDto) {
        try {
            Cart cart = cartService.addToCart(cartDto);
            return new ResponseEntity<>(cart, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/removeFromCart/{email}/{productId}")
    public ResponseEntity<Cart> removeFromCart(
            @PathVariable String email,
            @PathVariable String productId) {
        try {
            Cart cart = cartService.removeFromCart(email, productId);
            if (cart == null) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(cart, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/getCart/{email}")
    public ResponseEntity<Cart> getCart(@PathVariable String email) {
        try {
            Cart cart = cartService.getCartByEmail(email);
            if (cart == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(cart, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/clearCart/{email}")
    public ResponseEntity<String> clearCart(@PathVariable String email) {
        try {
            cartService.removeCartFromRedis(email);
            return new ResponseEntity<>("Cart cleared successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}