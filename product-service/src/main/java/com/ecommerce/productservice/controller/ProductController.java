package com.ecommerce.productservice.controller;

import com.ecommerce.productservice.dto.ProductDto;
import com.ecommerce.productservice.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("${productService.api.base.path}")
public class ProductController {
    @Autowired
    private ProductService productService;


    @PostMapping("/createProduct")
    public ResponseEntity<String> createProduct(@RequestBody ProductDto productDTO) throws Exception {
        productService.createProduct(productDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/updateProduct/{id}")
    public ResponseEntity<String> updateProduct(@PathVariable String id, @RequestBody ProductDto productDTO) throws NoSuchFieldException {
        productService.updateProduct(id, productDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/getProductById/{id}")
    public ResponseEntity<ProductDto> getProduct(@PathVariable String id) {
        Optional<ProductDto> productDtoOptional = productService.getProductById(id);
        return productDtoOptional
                .map(productDto -> new ResponseEntity<>(productDto, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/listAllProducts")
    public ResponseEntity<Page<ProductDto>> getAllProducts(@RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "10") int size) {
        return new ResponseEntity<Page<ProductDto>>(productService.getAllProducts(PageRequest.of(page, size))
                , HttpStatus.OK);
    }

    @DeleteMapping("deleteProductById/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable String id) throws NoSuchFieldException {
        if (productService.deleteProduct(id)){
            return new ResponseEntity<>("Product details deleted", HttpStatus.OK);
        }
        return new ResponseEntity<>("Details are not deleted for the given productId :: "+id,HttpStatus.NOT_FOUND);
    }
}
