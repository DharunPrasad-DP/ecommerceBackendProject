package com.ecommerce.productservice.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "products")
public class Product {

    @Id
    private String productId; // Format: MER001_P001
    private String name;
    private String description;
    private Double price;
    private Integer stock;
    private Long createdDate;
    private Long lastUpdatedTime;
}
