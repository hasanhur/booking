package com.example.bms.service;

import com.example.bms.entity.Appointment;

import java.util.List;

public interface AppointmentService {
    List<Appointment> getAllAppointments();
    List<Appointment> getAppointmentsByShopId(Long id);
    Appointment saveAppointment(Appointment appointment);
    Appointment getAppointmentById(Long id);
    Appointment updateAppointment(Appointment appointment);
    void deleteAppointmentById(Long id);
}
