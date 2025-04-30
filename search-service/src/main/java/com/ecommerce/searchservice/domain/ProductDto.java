package com.ecommerce.searchservice.domain;

import lombok.Data;
import org.apache.solr.client.solrj.beans.Field;

@Data
public class ProductDto {
    @Field("id")
    private String productId; // Format: MER001_P001
    @Field("productName")
    private String name;
    @Field("productDescription")
    private String description;
    @Field("productPrice")
    private Double price;
    @Field("stock")
    private Integer stock;
    @Field("createdDate")
    private Long createdDate; // Epoch time
    @Field("lastUpdatedTime")
    private Long lastUpdatedTime; // Epoch time
    @Field("merchantCode")
    private String merchantCode; // Format: MER001
    @Field("merchantRating")
    private Double merchantRating;
}
