package com.ecommerce.merchantservice.repository;

import com.ecommerce.merchantservice.domain.Merchant;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Map;

public interface MerchantRepository extends MongoRepository<Merchant,String> {
}
