package com.ecommerce.merchantservice.service;

import com.ecommerce.merchantservice.domain.Merchant;
import com.ecommerce.merchantservice.domain.MerchantEvent;
import com.ecommerce.merchantservice.dto.Merchantdto;
import com.ecommerce.merchantservice.repository.MerchantRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.Optional;

@Service
public class MerchantServiceImpl implements MerchantService {

    @Autowired
    private MerchantRepository merchantRepository;

    @Autowired
    private MerchantKafkaProducerService merchantKafkaProducerService;

    private Merchant convertToDomain(Merchantdto merchantdto) {
        Merchant merchant = new Merchant();
        BeanUtils.copyProperties(merchantdto, merchant);
        return merchant;
    }

    private Merchantdto convertDomainToDTO(Merchant merchant) {
        Merchantdto merchantdto = new Merchantdto();
        BeanUtils.copyProperties(merchant, merchantdto);
        return merchantdto;
    }

    @Override
    public void createMerchant(Merchantdto merchantdto) {
        merchantdto.setCreatedDate(Instant.now().toEpochMilli());
        merchantRepository.insert(convertToDomain(merchantdto));
    }

    @Override
    public void updateMerchant(String merchantId, Merchantdto merchantdto) throws NoSuchFieldException {
        Optional<Merchant> optionalProduct = merchantRepository.findById(merchantId);
        if (optionalProduct.isEmpty()) {
            throw new NoSuchFieldException("Merchant not found in DB");
        }

        Merchant merchant = optionalProduct.get();
        if (merchantdto.getName() != null) merchant.setName(merchantdto.getName());
        if (merchantdto.getEmail() != null) merchant.setEmail(merchantdto.getEmail());
        if (merchantdto.getPhoneNumber() != null) merchant.setPhoneNumber(merchantdto.getPhoneNumber());
        if (merchantdto.getAddress() != null) merchant.setAddress(merchantdto.getAddress());
        if (merchantdto.getRating() != null) merchant.setMerchantRating(merchantdto.getRating());
        merchant.setCreatedDate(Instant.now().toEpochMilli());

        //        Update to DB
        Merchant updatedMerchant = merchantRepository.save(merchant);
       //        fire kafka event
        MerchantEvent merchantEvent = new MerchantEvent();
        merchantEvent.setMerchantCode(updatedMerchant.getMerchantCode());
        merchantEvent.setMerchantRating(updatedMerchant.getMerchantRating());
        merchantKafkaProducerService.sendProductCreatedEvent(merchantEvent);
    }

    @Override
    public Optional<Merchantdto> getMerchantById(String id) {
        return merchantRepository.findById(id)
                .map(this::convertDomainToDTO);
    }

    @Override
    public Page<Merchantdto> getAllMerchants(Pageable pageable) {
        return merchantRepository.findAll(pageable)
                .map(this::convertDomainToDTO);
    }

    @Override
    public boolean deleteMerchantById(String id) throws NoSuchFieldException {
        Optional<Merchantdto> merchant = getMerchantById(id);
        if (merchant.isEmpty()) {
            throw new NoSuchFieldException("Merchant with ID " + id + " not found");
        }
        if (getMerchantById(id) != null) {
            merchantRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
