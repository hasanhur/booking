package com.example.bms.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static javax.persistence.CascadeType.ALL;

@Entity
public class Appservice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private BigDecimal price;

    @ManyToOne
    @JoinColumn(name="shop_id", nullable=false)
    private Shop shop;

    @OneToMany(cascade=ALL, mappedBy= "appservice")
    private List<Appointment> appointments;

    public Appservice(Long id, String name, String description, Shop shop, List<Appointment> appointments, BigDecimal price) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.shop = shop;
        this.appointments = appointments;
        this.price = price;
    }

    public Appservice() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    public List<Appointment> getAppointments() {
        return appointments;
    }

    public void setAppointments(List<Appointment> appointments) {
        this.appointments = appointments;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
