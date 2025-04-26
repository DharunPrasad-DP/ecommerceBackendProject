package com.ecommerce.merchantservice.controller;

import com.ecommerce.merchantservice.dto.Merchantdto;
import com.ecommerce.merchantservice.service.MerchantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("${merchantService.api.base.path}")
public class MerchantController {

    @Autowired
    private MerchantService merchantService;

    @PostMapping("/createMerchant")
    public ResponseEntity<String> createMerchant(@RequestBody Merchantdto merchantdto) throws Exception {
        merchantService.createMerchant(merchantdto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/updateMerchant/{id}")
    public ResponseEntity<String> updateMerchant(@PathVariable String id, @RequestBody Merchantdto merchantdto) throws NoSuchFieldException {
        merchantService.updateMerchant(id, merchantdto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/getMerchantById/{id}")
    public ResponseEntity<Merchantdto> getMerchantById(@PathVariable String id) {
        Optional<Merchantdto> merchantDtoOptional = merchantService.getMerchantById(id);
        return merchantDtoOptional
                .map(merchantdto -> new ResponseEntity<>(merchantdto, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/listAllMerchants")
    public ResponseEntity<Page<Merchantdto>> getAllMerchants(@RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "10") int size) {
        return new ResponseEntity<Page<Merchantdto>>(merchantService.getAllMerchants(PageRequest.of(page, size))
                , HttpStatus.OK);
    }

    @DeleteMapping("deleteMerchantById/{id}")
    public ResponseEntity<String> deleteMerchant(@PathVariable String id) throws NoSuchFieldException {
        if (merchantService.deleteMerchantById(id)){
            return new ResponseEntity<>("Merchant details deleted", HttpStatus.OK);
        }
        return new ResponseEntity<>("Details are not deleted for the given merchantId :: "+id,HttpStatus.NOT_FOUND);
    }
}
