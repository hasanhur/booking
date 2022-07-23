package com.example.bms.entity;

import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.sql.Time;
import java.sql.Timestamp;

@Entity
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "appointment_date")
    private Timestamp date;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status = Status.Created;

    @ManyToOne
    @JoinColumn(name="user_id", nullable=false)
    private User user;

    @ManyToOne
    @JoinColumn(name="appservice_id", nullable=false)
    private Appservice appservice;

    public Appointment(Long id, Timestamp date, Status status, User user, Appservice appservice) {
        this.id = id;
        this.date = date;
        this.status = status;
        this.user = user;
        this.appservice = appservice;
    }

    public Appointment() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Appservice getAppservice() {
        return appservice;
    }

    public void setAppservice(Appservice appservice) {
        this.appservice = appservice;
    }
}
