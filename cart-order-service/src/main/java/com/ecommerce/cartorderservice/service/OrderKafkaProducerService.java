package com.ecommerce.cartorderservice.service;

import com.ecommerce.cartorderservice.domain.OrderEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderKafkaProducerService {
    @Value("${order.topic.name}")
    private String productTopicName;

    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;

    public OrderKafkaProducerService(KafkaTemplate<String, OrderEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendProductCreatedEvent(OrderEvent event) {
        kafkaTemplate.send(productTopicName, event.getProductId(), event);
    }
}
