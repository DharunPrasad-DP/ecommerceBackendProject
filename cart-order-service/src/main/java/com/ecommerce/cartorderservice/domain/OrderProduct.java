package com.ecommerce.cartorderservice.domain;

import lombok.Data;

@Data
public class OrderProduct {
    private String productId;
    private int quantity;
    private double price;
}
