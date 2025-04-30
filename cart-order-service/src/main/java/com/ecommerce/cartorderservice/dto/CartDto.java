package com.ecommerce.cartorderservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CartDto {
    @NotBlank(message = "Email cannot be blank")
    private String email;

    @NotBlank(message = "Product ID cannot be blank")
    private String productId;

    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;

    private double price;
}
