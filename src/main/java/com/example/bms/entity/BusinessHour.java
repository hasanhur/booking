package com.example.bms.entity;

import javax.persistence.*;
import java.time.LocalTime;

@Entity
public class BusinessHour {
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "day")
    private Day day;

    @Column(name = "open_time")
    private LocalTime openTime;

    @Column(name = "close_time")
    private LocalTime closeTime;

    @ManyToOne
    @JoinColumn(name = "shop_id", nullable = false)
    private Shop shop;

    public BusinessHour(Long id, Day day, LocalTime openTime, LocalTime closeTime, Shop shop) {
        this.id = id;
        this.day = day;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.shop = shop;
    }

    public BusinessHour() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Day getDay() {
        return day;
    }

    public void setDay(Day day) {
        this.day = day;
    }

    public LocalTime getOpenTime() {
        return openTime;
    }

    public void setOpenTime(LocalTime openTime) {
        this.openTime = openTime;
    }

    public LocalTime getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(LocalTime closeTime) {
        this.closeTime = closeTime;
    }

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }
}
