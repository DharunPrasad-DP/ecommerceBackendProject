package com.ecommerce.productservice.service;

import com.ecommerce.productservice.domain.Product;
import com.ecommerce.productservice.domain.ProductEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ProductKafkaProducerService {
    @Value("${product.topic.name}")
    private String productTopicName;

    private final KafkaTemplate<String, ProductEvent> kafkaTemplate;

    public ProductKafkaProducerService(KafkaTemplate<String, ProductEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendCreateEvent(Product product) {
        ProductEvent event = new ProductEvent();
        event.setEventType("CREATE");
        event.setProduct(product);
        event.setTimestamp(System.currentTimeMillis());
        kafkaTemplate.send(productTopicName, product.getProductId(), event);
    }

    public void sendUpdateEvent(String productId, Map<String, Object> updatedFields) {
        ProductEvent event = new ProductEvent();
        event.setEventType("UPDATE");
        event.setProductId(productId);
        event.setUpdatedFields(updatedFields);
        event.setTimestamp(System.currentTimeMillis());
        kafkaTemplate.send(productTopicName, productId, event);
    }

    public void sendDeleteEvent(String productId) {
        ProductEvent event = new ProductEvent();
        event.setEventType("DELETE");
        event.setProductId(productId);
        event.setTimestamp(System.currentTimeMillis());
        kafkaTemplate.send(productTopicName, productId, event);
    }
}
