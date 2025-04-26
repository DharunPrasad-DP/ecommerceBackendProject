package com.ecommerce.productservice.service;

import com.ecommerce.productservice.domain.Product;
import com.ecommerce.productservice.dto.ProductDto;
import com.ecommerce.productservice.repository.ProductRepository;
import com.mongodb.DuplicateKeyException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
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
//        Validate if the merchant is present
        if (!isValidMerchant(productDTO.getProductId())){
            throw new RuntimeException("Merchant not found");
        }
        productDTO.setCreatedDate(Instant.now().toEpochMilli());
//        Save to DB
//        do proper error handling
            Product savedProduct = productRepository.insert(convertToDomain(productDTO));
//        Fire Kafka event
//        Create a separate logic to handle the CRUD case and create a payload to send in kafka
//        productKafkaProducerService.sendProductCreatedEvent(savedProduct);
    }

    @Override
    public void updateProduct(String productId, ProductDto productDTO) throws NoSuchFieldException {
//        The below is not required as the product will be in mongo already, but its better to have double check
        if (!isValidMerchant(productDTO.getProductId())){
            throw new RuntimeException("Merchant not found");
        }

        Optional<Product> optionalProduct = productRepository.findById(productId);
        if (optionalProduct.isEmpty()) {
            throw new NoSuchFieldException("Product not found in DB");
        }

        Product product = optionalProduct.get();
        if (productDTO.getName() != null) product.setName(productDTO.getName());
        if (productDTO.getDescription() != null) product.setDescription(productDTO.getDescription());
        if (productDTO.getPrice() != null) product.setPrice(productDTO.getPrice());
        if (productDTO.getStock() != null) product.setStock(productDTO.getStock());
        product.setCreatedDate(Instant.now().toEpochMilli());

        //        Update to DB
        Product updatedProduct = productRepository.save(product);

//        Fire Kafka event
//        Create a separate logic to handle the CRUD case and create a payload to send in kafka
//        productKafkaProducerService.sendProductCreatedEvent(updatedProduct); // For simplicity send whole object
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
            return true;
        }
        return false;
        //        fireDelete event(productId);
    }

    private boolean isValidMerchant(String productId) {
//        need modification
        String merchantCode = extractMerchantCode(productId); // Extract merchant code from productId
        String merchantServiceUrl = "http://localhost:8082/palaSarakku/merchant/getMerchantById/" + merchantCode; // Merchant Service API URL

        try {
            restTemplate.getForObject(merchantServiceUrl, Object.class); // Call Merchant Service API
            return true; // Merchant exists
        } catch (Exception e) {
            throw new RuntimeException("Invalid Merchant Code: " + merchantCode, e); // Merchant not found or API call failed
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
                .map(this::convertDomainToDTO); // Convert Product to ProductDto
    }
}
