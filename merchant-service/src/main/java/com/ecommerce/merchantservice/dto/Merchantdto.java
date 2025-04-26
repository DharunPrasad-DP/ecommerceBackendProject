package com.ecommerce.merchantservice.dto;

import lombok.Data;

@Data
public class Merchantdto {
    private String merchantCode;
    private String name;
    private String email;
    private String phoneNumber;
    private String address;
    private Long createdDate;
    private Double rating;
}
