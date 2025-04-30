package com.ecommerce.productservice.domain;

import lombok.Data;

@Data
public class OrderEvent {
    private String productId;
    private int quantity;
}
