package com.ecommerce.searchservice.service;

import com.ecommerce.searchservice.domain.Product;
import com.ecommerce.searchservice.domain.ProductDto;
import org.apache.solr.client.solrj.SolrServerException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface SearchSolrCrudService {
    void addProduct(Product product) throws SolrServerException, IOException;

    Product getProductById(String id) throws SolrServerException, IOException;

    boolean deleteProductById(String id) throws SolrServerException, IOException;

    Product atomicUpdateProduct(String id, Map<String, Object> updates) throws SolrServerException, IOException;

    void updateDocumentsByField(String queryField, Object queryValue, String updateField, Object updateValue) throws SolrServerException, IOException;

    void updateProductStock(String productId, int quantity);

    List<ProductDto> searchProductsByName(String productName);
}
