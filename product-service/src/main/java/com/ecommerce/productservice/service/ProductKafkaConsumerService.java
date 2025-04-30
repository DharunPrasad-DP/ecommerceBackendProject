package com.ecommerce.productservice.service;

import com.ecommerce.productservice.domain.OrderEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ProductKafkaConsumerService {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductService productService;

    @KafkaListener(topics = "${order.topic.name}", groupId = "product-service-group")
    public void consumeOrderEvent(String event) throws JsonProcessingException, NoSuchFieldException {
        OrderEvent orderEvent = objectMapper.readValue(event, OrderEvent.class);
        productService.updateProductStock(orderEvent.getProductId(), orderEvent.getQuantity());
    }
}
