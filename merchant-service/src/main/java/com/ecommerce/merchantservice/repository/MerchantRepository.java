package com.ecommerce.merchantservice.repository;

import com.ecommerce.merchantservice.domain.Merchant;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MerchantRepository extends MongoRepository<Merchant,String> {
}
