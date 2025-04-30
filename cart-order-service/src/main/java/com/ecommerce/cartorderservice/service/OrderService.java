package com.ecommerce.cartorderservice.service;

import com.ecommerce.cartorderservice.domain.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    Order createOrder(String email) throws Exception;
    Page<Order> getOrdersByEmail(String email, Pageable pageable);
}
