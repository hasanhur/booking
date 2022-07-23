package com.example.bms.service;

import com.example.bms.entity.Appservice;

import java.util.List;

public interface AppserviceService {
    List<Appservice> getAllAppservices();
    Appservice saveAppservice(Appservice appservice);
    Appservice getAppserviceById(Long id);
    Appservice updateAppservice(Appservice appservice);
    void deleteAppserviceById(Long id);
}
