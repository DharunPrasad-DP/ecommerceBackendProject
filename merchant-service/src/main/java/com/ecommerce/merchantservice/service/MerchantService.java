package com.ecommerce.merchantservice.service;

import com.ecommerce.merchantservice.dto.Merchantdto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface MerchantService {
    void createMerchant(Merchantdto merchantdto);

    void updateMerchant(String id, Merchantdto merchantdto) throws NoSuchFieldException;

    Optional<Merchantdto> getMerchantById(String id);

    Page<Merchantdto> getAllMerchants(Pageable pageable);

    boolean deleteMerchantById(String id) throws NoSuchFieldException;
}
