package com.example.bms.repository;

import com.example.bms.entity.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface ShopRepository extends JpaRepository<Shop, Long> {
}
