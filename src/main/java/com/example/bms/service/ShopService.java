package com.example.bms.service;

import com.example.bms.entity.Shop;

import java.util.List;

public interface ShopService {
    List<Shop> getAllShops();
    Shop saveShop(Shop shop);
    Shop getShopById(Long id);
    Shop updateShop(Shop shop);
    void deleteShopById(Long id);
}
