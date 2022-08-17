package com.example.bms.service;

import com.example.bms.entity.BusinessHour;
import com.example.bms.entity.Day;
import com.example.bms.entity.Shop;

import java.sql.Timestamp;
import java.time.LocalTime;
import java.util.List;

public interface ShopService {
    List<Shop> getAllShops();
    Shop saveShop(Shop shop);
    Shop getShopById(Long id);
    Shop updateShop(Shop shop);
    void deleteShopById(Long id);
    BusinessHour getBusinessHour(Long shopId, Day day);
    BusinessHour saveBusinessHour(BusinessHour businessHour);
    BusinessHour updateBusinessHour(BusinessHour businessHour);
    boolean checkIfShopIsOpen(Long shopId, LocalTime appointmentTime, String day);

}
