package com.ecommerce.cartorderservice.domain;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "orders")
public class CartItem {
    private String productId;
    private int quantity;
    private double price;
}