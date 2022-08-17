package com.example.bms.repository;

import com.example.bms.entity.Appservice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface AppserviceRepository extends JpaRepository<Appservice, Long> {
}
