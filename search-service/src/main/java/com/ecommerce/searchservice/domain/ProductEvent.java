package com.ecommerce.searchservice.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductEvent {
    private String eventType; // CREATE, UPDATE, DELETE
    private String productId;
    private Product product; // For CREATE events
    private Map<String, Object> updatedFields; // For UPDATE events
    private Long timestamp;
}