package com.example.bms.service.impl;

import com.example.bms.entity.Shop;
import com.example.bms.repository.ShopRepository;
import com.example.bms.service.ShopService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ShopServiceImpl implements ShopService {
    private ShopRepository shopRepository;

    public ShopServiceImpl(ShopRepository shopRepository) {
        super();
        this.shopRepository = shopRepository;
    }

    @Cacheable("shops")
    @Override
    public List<Shop> getAllShops() {
        log.info("Fetching all shops from the database");
        return shopRepository.findAll();
    }

    @CachePut(value = "shop", key = "#shop.id")
    @Override
    public Shop saveShop(Shop shop) {
        log.info("Saving new shop {} to the database", shop.getName());
        return shopRepository.save(shop);
    }

    @Override
    public Shop getShopById(Long id) {
        log.info("Fetching shop by id {}", id);
        return shopRepository.findById(id).get();
    }

    @CachePut(value = "shop", key = "#shop.id")
    @Override
    public Shop updateShop(Shop shop) {
        log.info("Updating shop {}", shop.getName());
        return shopRepository.save(shop);
    }

    @CacheEvict(value = "shop", key = "#id")
    @Override
    public void deleteShopById(Long id) {
        log.info("Deleting shop by id {}", id);
        shopRepository.deleteById(id);
    }
}
