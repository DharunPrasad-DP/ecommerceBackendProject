package com.ecommerce.productservice.service;

import com.ecommerce.productservice.domain.Product;
import com.ecommerce.productservice.dto.ProductDto;
import com.ecommerce.productservice.repository.ProductRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductKafkaProducerService productKafkaProducerService;

    private final RestTemplate restTemplate = new RestTemplate();

    private Product convertToDomain(ProductDto productDTO) {
        Product product = new Product();
        BeanUtils.copyProperties(productDTO, product);
        return product;
    }

    private ProductDto convertDomainToDTO(Product product) {
        ProductDto productDetailsDto = new ProductDto();
        BeanUtils.copyProperties(product, productDetailsDto);
        return productDetailsDto;
    }

    @Override
    public void createProduct(ProductDto productDTO) throws Exception {
//        Check if the merchant is present
        if (!isValidMerchant(productDTO.getProductId())){
            throw new RuntimeException("Merchant not found");
        }
        productDTO.setCreatedDate(Instant.now().toEpochMilli());
        productDTO.setLastUpdatedTime(Instant.now().toEpochMilli());
//        Save to DB
            Product savedProduct = productRepository.insert(convertToDomain(productDTO));
//        Fire Kafka event
        productKafkaProducerService.sendCreateEvent(savedProduct);
    }

    @Override
    public void updateProduct(String productId, ProductDto productDTO) throws NoSuchFieldException {
//        The below is not required as the product will be in Mongo already, but it's better to have double check
        if (!isValidMerchant(productDTO.getProductId())){
            throw new RuntimeException("Merchant not found");
        }

        Optional<Product> optionalProduct = productRepository.findById(productId);
        if (optionalProduct.isEmpty()) {
            throw new NoSuchFieldException("Product not found in DB");
        }

        Map<String, Object> updatedFields = new HashMap<>();

        Product product = optionalProduct.get();
        if (productDTO.getName() != null && !productDTO.getName().equals(product.getName())) {
            product.setName(productDTO.getName());
            updatedFields.put("productName", productDTO.getName());
        }
        if (productDTO.getDescription() != null && !productDTO.getDescription().equals(product.getDescription())){
            product.setDescription(productDTO.getDescription());
            updatedFields.put("productDescription", productDTO.getDescription());
        }
        if (productDTO.getPrice() != null && !productDTO.getPrice().equals(product.getPrice())){
            product.setPrice(productDTO.getPrice());
            updatedFields.put("productPrice", productDTO.getPrice());
        }
        if (productDTO.getStock() != null && !productDTO.getStock().equals(product.getStock())) {
            product.setStock(productDTO.getStock());
            updatedFields.put("stock", productDTO.getStock());
        }
        product.setLastUpdatedTime(Instant.now().toEpochMilli());
        //        Update to DB
        Product updatedProduct = productRepository.save(product);
        //        Fire Kafka event
        if (!updatedFields.isEmpty()) {
            productKafkaProducerService.sendUpdateEvent(productId, updatedFields);
        }
    }

    @Override
    public Optional<ProductDto> getProductById(String id) {
        return productRepository.findById(id)
                .map(this::convertDomainToDTO);
    }

    @Override
    public boolean deleteProduct(String id) throws NoSuchFieldException {
        Optional<ProductDto> product = getProductById(id);
        if (product.isEmpty()) {
            throw new NoSuchFieldException("Product with ID " + id + " not found");
        }
        if (getProductById(id) != null) {
            productRepository.deleteById(id);
            productKafkaProducerService.sendDeleteEvent(id);
            return true;
        }
        return false;
    }

    @Override
    public void updateProductStock(String productId, int quantity) throws NoSuchFieldException {
        Optional<Product> optionalProduct = productRepository.findById(productId);
        if (optionalProduct.isEmpty()) {
            throw new NoSuchFieldException("Product not found in DB");
        }

        Product product = optionalProduct.get();
        int currentStock = product.getStock();
        if (currentStock < quantity) {
            throw new NoSuchFieldException("Insufficient stock for product ID: " + productId);
        }

        product.setStock(currentStock - quantity);
        product.setLastUpdatedTime(Instant.now().toEpochMilli());
        productRepository.save(product);
    }

    private boolean isValidMerchant(String productId) {
        String merchantCode = extractMerchantCode(productId); // Extract merchant code from productId
        String merchantServiceUrl = "http://localhost:8082/palaSarakku/merchant/getMerchantById/" + merchantCode;

        try {
            restTemplate.getForObject(merchantServiceUrl, Object.class); // Call Merchant Service API
            return true; // Merchant exists
        } catch (Exception e) {
            throw new RuntimeException("Invalid Merchant Code: " + merchantCode, e);
        }
    }

    private String extractMerchantCode(String productId) {
        if (productId == null || !productId.contains("_")) {
            throw new RuntimeException("Invalid Product ID format");
        }
        return productId.split("_")[0];
    }

    @Override
    public Page<ProductDto> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(this::convertDomainToDTO);
    }
}
