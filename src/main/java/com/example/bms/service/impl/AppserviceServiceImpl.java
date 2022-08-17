package com.example.bms.service.impl;

import com.example.bms.entity.Appservice;
import com.example.bms.repository.AppserviceRepository;
import com.example.bms.service.AppserviceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class AppserviceServiceImpl implements AppserviceService {
    private AppserviceRepository appserviceRepository;

    public AppserviceServiceImpl(AppserviceRepository appserviceRepository) {
        super();
        this.appserviceRepository = appserviceRepository;
    }

    @Override
    public List<Appservice> getAllAppservices() {
        log.info("Fetching all services from the database");
        return appserviceRepository.findAll();
    }

    @Override
    public Appservice saveAppservice(Appservice appservice) {
        log.info("Saving new service {} to the database", appservice.getName());
        return appserviceRepository.save(appservice);
    }

    @Override
    public Appservice getAppserviceById(Long id) {
        log.info("Fetching service by id {}", id);
        return appserviceRepository.findById(id).get();
    }

    @Override
    public Appservice updateAppservice(Appservice appservice) {
        log.info("Updating service {}", appservice.getName());
        return appserviceRepository.save(appservice);
    }

    @Override
    public void deleteAppserviceById(Long id) {
        log.info("Deleting service by id {}", id);
        appserviceRepository.deleteById(id);
    }
}
