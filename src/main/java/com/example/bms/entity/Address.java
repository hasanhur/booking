package com.example.bms.entity;

import javax.persistence.*;

@Entity
public class Address {
    @Id
    @GeneratedValue
    private Long id;

    private String body;

    private String city;

    private String country;

    private String postCode;

    @OneToOne
    @JoinColumn(name="shop_id", nullable=false)
    private Shop shop;

    public Address(Long id, String body, String city, String country, String postCode, Shop shop) {
        this.id = id;
        this.body = body;
        this.city = city;
        this.country = country;
        this.postCode = postCode;
        this.shop = shop;
    }

    public Address() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }
}
