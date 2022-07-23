package com.example.bms.service.impl;

import com.example.bms.entity.Appointment;
import com.example.bms.repository.AppointmentRepository;
import com.example.bms.service.AppointmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class AppointmentServiceImpl implements AppointmentService {
    private AppointmentRepository appointmentRepository;

    public AppointmentServiceImpl(AppointmentRepository appointmentRepository) {
        super();
        this.appointmentRepository = appointmentRepository;
    }

    @Cacheable("appointments")
    @Override
    public List<Appointment> getAllAppointments() {
        log.info("Fetching all appointments from the database");
        return appointmentRepository.findAll();
    }

    @Override
    public List<Appointment> getAppointmentsByShopId(Long id) {
        log.info("Fetching all appointments of shop by id {}", id);
        return appointmentRepository.findAllByShopId(id);
    }

    @CachePut(value = "appointment", key = "#id")
    @Override
    public Appointment saveAppointment(Appointment appointment) {
        log.info("Saving new appointment by id {} to the database", appointment.getId());
        return appointmentRepository.save(appointment);
    }

    @Override
    public Appointment getAppointmentById(Long id) {
        log.info("Fetching appointment by id {}", id);
        return appointmentRepository.findById(id).get();
    }

    @CachePut(value = "appointment", key = "#id")
    @Override
    public Appointment updateAppointment(Appointment appointment) {
        log.info("Updating appointment by id {}", appointment.getId());
        return appointmentRepository.save(appointment);
    }

    @CacheEvict(value = "appointment", key = "#id")
    @Override
    public void deleteAppointmentById(Long id) {
        log.info("Deleting appointment by id {}", id);
        appointmentRepository.deleteById(id);
    }
}
