package com.ecommerce.cartorderservice.repository;

import com.ecommerce.cartorderservice.domain.Cart;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CartRepository extends MongoRepository<Cart, String> {
    Cart findByEmail(String email);

    void deleteByEmail(String email);
}
