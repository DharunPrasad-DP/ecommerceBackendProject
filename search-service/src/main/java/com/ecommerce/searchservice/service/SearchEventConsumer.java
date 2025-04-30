package com.ecommerce.searchservice.service;

import com.ecommerce.searchservice.domain.MerchantEvent;
import com.ecommerce.searchservice.domain.OrderEvent;
import com.ecommerce.searchservice.domain.ProductEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class SearchEventConsumer {

    @Autowired
    private SearchSolrCrudService solrService;

    @Autowired
    private ObjectMapper objectMapper;

    @KafkaListener(topics = "${product.topic.name}", groupId = "search-service-group")
    public void consumeProductEvent(String event) throws SolrServerException, IOException {
        ProductEvent productEvent = objectMapper.readValue(event, ProductEvent.class);
        switch (productEvent.getEventType()) {
            case "CREATE":
                System.out.println("CREATE" + productEvent);
                solrService.addProduct(productEvent.getProduct());
                break;
            case "UPDATE":
                System.out.println("UPDATE" + productEvent);
                solrService.atomicUpdateProduct(productEvent.getProductId(), productEvent.getUpdatedFields());
                break;
            case "DELETE":
                System.out.println("DELETE" + productEvent);
                solrService.deleteProductById(productEvent.getProductId());
                break;
        }
    }

    @KafkaListener(topics = "${merchant.topic.name}", groupId = "search-service-group")
    public void consumeMerchantRatingEvent(String event) throws IOException, SolrServerException {
        MerchantEvent merchantEvent = objectMapper.readValue(event, MerchantEvent.class);
        solrService.updateDocumentsByField("merchantCode", merchantEvent.getMerchantCode(), "merchantRating", merchantEvent.getMerchantRating());
    }

    @KafkaListener(topics = "${order.topic.name}", groupId = "search-service-group")
    public void consumeOrderEvent(String event) throws JsonProcessingException, NoSuchFieldException {
        OrderEvent orderEvent = objectMapper.readValue(event, OrderEvent.class);
        solrService.updateProductStock(orderEvent.getProductId(), orderEvent.getQuantity());
    }
}
