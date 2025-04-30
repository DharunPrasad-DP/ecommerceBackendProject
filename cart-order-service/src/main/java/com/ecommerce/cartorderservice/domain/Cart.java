package com.ecommerce.cartorderservice.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "cart")
@Data
public class Cart {
    @Id
    private String email;
    private List<CartItem> cartItems;
}
