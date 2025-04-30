package com.ecommerce.cartorderservice.repository;

import com.ecommerce.cartorderservice.domain.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface OrderRepository extends MongoRepository<Order, String> {
    Page<Order> findByEmailOrderByOrderIdDesc(String email, Pageable pageable);
}
