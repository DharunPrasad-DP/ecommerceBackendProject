package com.ecommerce.merchantservice.service;

import com.ecommerce.merchantservice.domain.Merchant;
import com.ecommerce.merchantservice.domain.MerchantEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class MerchantKafkaProducerService {
    @Value("${merchant.topic.name}")
    private String merchantTopicName;

    private final KafkaTemplate<String, MerchantEvent> kafkaTemplate;

    public MerchantKafkaProducerService(KafkaTemplate<String, MerchantEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendProductCreatedEvent(MerchantEvent event) {
        kafkaTemplate.send(merchantTopicName, event.getMerchantCode(), event);
    }
}
