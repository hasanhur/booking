package com.example.bms.entity;

import javax.persistence.*;
import java.util.List;

import static javax.persistence.CascadeType.ALL;

@Entity
public class Shop {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "image")
    private String image;

    @OneToMany(cascade=ALL, mappedBy="shop")
    private List<Appservice> appservices;

    @ManyToOne
    @JoinColumn(name="user_id", nullable=false)
    private User user;

    @OneToMany(cascade=ALL, mappedBy="shop")
    private List<BusinessHour> businessHours;

    @OneToOne
    @JoinColumn(name="address_id")
    private Address address;

    public Shop(Long id, String name, String description, String image, List<Appservice> appservices, User user, List<BusinessHour> businessHours, Address address) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.image = image;
        this.appservices = appservices;
        this.user = user;
        this.businessHours = businessHours;
        this.address = address;
    }

    public Shop() {
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<Appservice> getAppservices() {
        return appservices;
    }

    public void setAppservices(List<Appservice> appservices) {
        this.appservices = appservices;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<BusinessHour> getBusinessHours() {
        return businessHours;
    }

    public void setBusinessHours(List<BusinessHour> businessHours) {
        this.businessHours = businessHours;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}
