package com.ecommerce.searchservice.domain;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MerchantEvent {
    private String merchantCode;
    private Double merchantRating;
}
