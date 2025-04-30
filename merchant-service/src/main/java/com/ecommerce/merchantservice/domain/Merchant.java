package com.ecommerce.merchantservice.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "merchants")
public class Merchant {
    @Id
    private String merchantCode;
    private String name;
    private String email;
    private String phoneNumber;
    private String address;
    private Long createdDate;
    private Double merchantRating;
}
