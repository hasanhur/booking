package com.example.bms.service.impl;

import com.example.bms.entity.BusinessHour;
import com.example.bms.entity.Day;
import com.example.bms.entity.Shop;
import com.example.bms.repository.BusinessHourRepository;
import com.example.bms.repository.ShopRepository;
import com.example.bms.service.ShopService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;

@Slf4j
@Service
public class ShopServiceImpl implements ShopService {
    private ShopRepository shopRepository;
    private BusinessHourRepository businessHourRepository;

    public ShopServiceImpl(ShopRepository shopRepository, BusinessHourRepository businessHourRepository) {
        super();
        this.shopRepository = shopRepository;
        this.businessHourRepository = businessHourRepository;
    }

    @Override
    public List<Shop> getAllShops() {
        log.info("Fetching all shops from the database");
        return shopRepository.findAll();
    }

    @Override
    public Shop saveShop(Shop shop) {
        log.info("Saving new shop {} to the database", shop.getName());
        return shopRepository.save(shop);
    }

    @Override
    public Shop getShopById(Long id) {
        log.info("Fetching shop by id {}", id);
        return shopRepository.findById(id).orElse(null);
    }

    @Override
    public Shop updateShop(Shop shop) {
        log.info("Updating shop {}", shop.getName());
        return shopRepository.save(shop);
    }

    @Override
    public void deleteShopById(Long id) {
        log.info("Deleting shop by id {}", id);
        shopRepository.deleteById(id);
    }

    @Override
    public BusinessHour getBusinessHour(Long shopId, Day day) {
        log.info("Fetching business hour by shop id: {} and day: {}", shopId, day);
        return businessHourRepository.findByShopIdAndDay(shopId, day);
    }

    @Override
    public BusinessHour saveBusinessHour(BusinessHour businessHour) {
        log.info("Saving a new business hour to the database");
        return businessHourRepository.save(businessHour);
    }

    @Override
    public BusinessHour updateBusinessHour(BusinessHour businessHour) {
        log.info("Updating business hour {}", businessHour.getId());
        return businessHourRepository.save(businessHour);
    }

    @Override
    public boolean checkIfShopIsOpen(Long shopId, LocalTime appointmentTime, String day) {
        log.info("Checking if shop {} is open at {} day {}", shopId, appointmentTime, day);
        if (businessHourRepository.getOpenHours(shopId, appointmentTime, day) > 0) {
            return true;
        }
        return false;
    }
}
