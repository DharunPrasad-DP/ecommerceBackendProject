package com.ecommerce.productservice.service;

import com.ecommerce.productservice.dto.ProductDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ProductService {
    void createProduct(ProductDto productDto) throws Exception;

    Optional<ProductDto> getProductById(String productId);

    Page<ProductDto> getAllProducts(Pageable pageable);

    void updateProduct(String productId, ProductDto productDto) throws NoSuchFieldException;

    boolean deleteProduct(String productId) throws NoSuchFieldException;
}
