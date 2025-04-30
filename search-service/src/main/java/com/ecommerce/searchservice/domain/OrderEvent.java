package com.ecommerce.searchservice.domain;

import lombok.Data;

@Data
public class OrderEvent {
    private String productId;
    private int quantity;
}
