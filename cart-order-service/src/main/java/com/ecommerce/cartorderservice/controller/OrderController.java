package com.ecommerce.cartorderservice.controller;

import com.ecommerce.cartorderservice.domain.Order;
import com.ecommerce.cartorderservice.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${cartOrderService.api.base.path}")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/checkout/{email}")
    public ResponseEntity<Order> createOrder(@PathVariable String email) {
        try {
            Order order = orderService.createOrder(email);
            return new ResponseEntity<>(order, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/history/{email}")
    public ResponseEntity<Page<Order>> getOrderHistory(
            @PathVariable String email,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<Order> orders = orderService.getOrdersByEmail(email, PageRequest.of(page, size));
            return new ResponseEntity<>(orders, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}