package com.ecommerce.merchantservice.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "merchants")
public class Merchant {
    @Id
    @NotBlank(message = "Merchant code cannot be blank or null")
    @Pattern(
            regexp = "^[A-Z]{3}-\\d{3}$",
            message = "Merchant code must be in format 'MER-001' (3 uppercase letters - 3 digits)"
    )
    private String merchantCode;
    private String name;
    private String email;
    private String phoneNumber;
    private String address;
    private Long createdDate;
    private Double rating;
}
