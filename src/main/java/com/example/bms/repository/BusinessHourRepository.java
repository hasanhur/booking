package com.example.bms.repository;

import com.example.bms.entity.BusinessHour;
import com.example.bms.entity.Day;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalTime;

@Transactional
public interface BusinessHourRepository extends JpaRepository<BusinessHour, Long> {
    BusinessHour findByShopIdAndDay(Long shopId, Day day);

    @Query(value = "SELECT COUNT(id) FROM business_hour " +
            "WHERE open_time <= :appointmentTime " +
            "AND close_time >= :appointmentTime " +
            "AND shop_id = :shopId " +
            "AND day = :day"
            , nativeQuery = true)
    int getOpenHours(@Param("shopId") Long shopId, @Param("appointmentTime") LocalTime appointmentTime, @Param("day") String day);
}
