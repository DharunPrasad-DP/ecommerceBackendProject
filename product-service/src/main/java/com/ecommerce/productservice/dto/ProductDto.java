package com.ecommerce.productservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ProductDto {

    @NotBlank(message = "Product id cannot be blank or null")
    @Pattern(
            regexp = "^[A-Z]{3}-\\d{3}_[A-Z]{4}-\\d{3}$",
            message = "Product ID must be in format 'MER-001_PROD-001'"
    )
    private String productId;
    private String name;
    private String description;
    private Double price;
    private Integer stock;
    private Long createdDate;
    private Long lastUpdatedTime;
}
