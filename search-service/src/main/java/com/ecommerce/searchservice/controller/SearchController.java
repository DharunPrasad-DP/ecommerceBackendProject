package com.ecommerce.searchservice.controller;

import com.ecommerce.searchservice.domain.ProductDto;
import com.ecommerce.searchservice.service.SearchSolrCrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("${searchService.api.base.path}")
public class SearchController {
    @Autowired
    private SearchSolrCrudService searchSolrCrudService;

    @GetMapping("/searchProducts")
    public ResponseEntity<List<ProductDto>> searchProductsByName(@RequestParam("query") String productName) {
        List<ProductDto> products = searchSolrCrudService.searchProductsByName(productName);
        return ResponseEntity.ok(products);
    }
}
