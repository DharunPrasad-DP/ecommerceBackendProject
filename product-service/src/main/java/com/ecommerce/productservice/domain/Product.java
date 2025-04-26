package com.ecommerce.productservice.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "products")
public class Product {

    @Id
    @NotBlank(message = "Product id cannot be blank or null")
    @Pattern(
            regexp = "^[A-Z]{3}-\\d{3}_[A-Z]{4}-\\d{3}$",
            message = "Product ID must be in format 'MER-001_PROD-001'"
    )
    private String productId; // Format: MER001_P001
    private String name;
    private String description;
    private Double price;
    private Integer stock;
    private Long createdDate; // Epoch time
}
