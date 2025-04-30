package com.ecommerce.searchservice.service;

import com.ecommerce.searchservice.domain.Product;
import com.ecommerce.searchservice.domain.ProductDto;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SearchSolrCrudServiceImpl implements SearchSolrCrudService {

    private final String collection = "products_catalog_search";

    @Autowired
    private SolrClient solrClient;

    @Override
    public void addProduct(Product product) throws SolrServerException, IOException {
        product.setLastUpdatedTime(System.currentTimeMillis());
        product.setMerchantCode(product.getProductId().split("_")[0]);
        solrClient.addBean(collection, product);
        solrClient.commit(collection);
    }

    @Override
    public Product getProductById(String id) throws SolrServerException, IOException {
        SolrQuery query = new SolrQuery("id:" + id);
        QueryResponse response = solrClient.query(collection, query);
        List<Product> products = response.getBeans(Product.class);
        return products.isEmpty() ? null : products.get(0);
    }

    @Override
    public boolean deleteProductById(String id) throws SolrServerException, IOException {
        if (getProductById(id) != null) {
            solrClient.deleteById(collection, id);
            solrClient.commit(collection);
            return true;
        }
        return false;
    }

    @Override
    public Product atomicUpdateProduct(String id, Map<String, Object> updates) throws SolrServerException, IOException {
        SolrInputDocument solrDoc = new SolrInputDocument();
        solrDoc.addField("id", id); // Primary key

        Map<String, Object> fieldUpdate;
        for (Map.Entry<String, Object> entry : updates.entrySet()) {
            fieldUpdate = new HashMap<>();
            fieldUpdate.put("set", entry.getValue()); // Atomic update operation
            solrDoc.addField(entry.getKey(), fieldUpdate);
        }

        fieldUpdate = new HashMap<>();
        fieldUpdate.put("set", System.currentTimeMillis());
        solrDoc.addField("lastUpdatedTime", fieldUpdate);

        solrClient.add(collection, solrDoc);
        solrClient.commit(collection);

        return getProductById(id);
    }

    @Override
    public void updateDocumentsByField(String queryField, Object queryValue, String updateField, Object updateValue) throws SolrServerException, IOException {
        // query to fetch documents based on the query field and value
        SolrQuery query = new SolrQuery();
        query.setQuery(queryField + ":" + queryValue);

        QueryResponse response = solrClient.query(collection, query);
        List<String> documentIds = response.getResults().stream()
                .map(doc -> (String) doc.getFieldValue("id"))
                .toList();

        // Update each document with the update field and value
        for (String id : documentIds) {
            SolrInputDocument solrDoc = new SolrInputDocument();
            solrDoc.addField("id", id);

            Map<String, Object> fieldUpdate = new HashMap<>();
            fieldUpdate.put("set", updateValue);
            solrDoc.addField(updateField, fieldUpdate);

            fieldUpdate = new HashMap<>();
            fieldUpdate.put("set", System.currentTimeMillis());
            solrDoc.addField("lastUpdatedTime", fieldUpdate);

            solrClient.add(collection, solrDoc);
        }
        solrClient.commit(collection);
    }

    @Override
    public void updateProductStock(String productId, int quantity) {
        try {
            SolrInputDocument solrDoc = new SolrInputDocument();
            solrDoc.addField("id", productId);

         // Decrement the stock field by the given quantity
            Map<String, Object> fieldUpdate = new HashMap<>();
            fieldUpdate.put("inc", -quantity);
            solrDoc.addField("stock", fieldUpdate);

            fieldUpdate = new HashMap<>();
            fieldUpdate.put("set", System.currentTimeMillis());
            solrDoc.addField("lastUpdatedTime", fieldUpdate);

            solrClient.add(collection, solrDoc);

            solrClient.commit(collection);
        } catch (SolrServerException | IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to update product stock for productId: " + productId, e);
        }
    }

    @Override
    public List<ProductDto> searchProductsByName(String productName) {
        try {
            productName = escapeSolrQueryChars(productName);
            if (productName.contains(" ")) {
                productName = "\"" + productName + "\"";
            }

            SolrQuery query = new SolrQuery();
            query.setQuery("productName:" + productName);
            query.setStart(0);
            query.setRows(10);
            QueryResponse response = solrClient.query(collection, query);

            return response.getBeans(ProductDto.class);
        } catch (SolrServerException | IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to search products by name: " + productName, e);
        }
    }

    private String escapeSolrQueryChars(String input) {
        return input.replaceAll("([+\\-!(){}\\[\\]^\"~*?:\\\\/])", "\\\\$1");
    }
}
