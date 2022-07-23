package com.example.bms.repository;

import com.example.bms.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
   @Query(value = "SELECT * FROM appointment WHERE appservice_id IN (SELECT id FROM appservice WHERE shop_id = :id);", nativeQuery = true)
    public List<Appointment> findAllByShopId(@Param("id") Long id);
}
