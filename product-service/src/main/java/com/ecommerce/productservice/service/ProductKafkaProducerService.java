package com.ecommerce.productservice.service;

import com.ecommerce.productservice.domain.ProductEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ProductKafkaProducerService {
    @Value("${product.topic.name}")
    private String productTopicName;

    private final KafkaTemplate<String, ProductEvent> kafkaTemplate;

    public ProductKafkaProducerService(KafkaTemplate<String, ProductEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendProductCreatedEvent(ProductEvent event) {
        kafkaTemplate.send(productTopicName, event.getProductId(), event);
    }
}
