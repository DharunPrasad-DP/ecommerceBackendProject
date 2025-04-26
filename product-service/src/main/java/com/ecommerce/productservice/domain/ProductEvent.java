package com.ecommerce.productservice.domain;

import com.ecommerce.productservice.dto.ProductDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductEvent {
    private String productId;
    private String eventType; // "CREATE", "UPDATE", etc
    private ProductDto productDto; // Send the ProductDto as part of event
}