package com.ecommerce.merchantservice.domain;

import com.ecommerce.merchantservice.dto.Merchantdto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MerchantEvent {
    private String merchantCode;
    private String eventType; // "CREATE", "UPDATE", etc
    private Merchantdto merchantdto; // Send the merchantDto as part of event
}
