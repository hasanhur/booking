package com.example.bms.repository;

import com.example.bms.entity.Appointment;
import com.example.bms.entity.Status;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.sql.Timestamp;
import java.util.NoSuchElementException;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AppointmentRepositoryTest {

    @Autowired private AppointmentRepository appointmentRepository;
    @Autowired private AppserviceRepository appserviceRepository;

    @Test
    void injectedComponentsAreNotNull() {
        Assertions.assertNotNull(appointmentRepository);
        Assertions.assertNotNull(appserviceRepository);
    }

    @Test
    void anyAppointmentExists( ) {
        Assertions.assertNotNull(appointmentRepository.findAll());
    }

    @Test
    void anyAppointmentByShopIdExists() {
        Assertions.assertTrue(appointmentRepository.findAllByShopId(1L).isEmpty());
        Assertions.assertNotNull(appointmentRepository.findAllByShopId(5L));
    }

    @Test
    void appointmentWithGivenIdExists() {
        Assertions.assertTrue(appointmentRepository.findById(4L).isPresent());
    }

    @Test
    void newAppointmentSaved() {
        Appointment appointment = new Appointment();
        appointment.setAppservice(appserviceRepository.findById(3L).get());
        appointment.setUser(appointmentRepository.findById(7L).get().getUser());
        appointment.setDate(Timestamp.valueOf("2022-08-10 11:23:00"));
        appointment.setStatus(Status.Created);
        Assertions.assertEquals(appointmentRepository.save(appointment), appointment);
    }

    @Test
    void appointmentUpdated() {
        Appointment appointment = appointmentRepository.findById(4L).get();

        Status expectedStatus = Status.Created;
        Assertions.assertEquals(appointment.getStatus(), expectedStatus);

        Status updatedStatus = Status.Cancelled;
        appointment.setStatus(Status.Cancelled);

        Timestamp expectedDate = Timestamp.valueOf("2022-08-23 14:06:00");
        appointment.setDate(Timestamp.valueOf("2022-08-23 14:06:00"));
        appointmentRepository.save(appointment);

        Assertions.assertEquals(appointmentRepository.findById(4L).get().getStatus(), updatedStatus);
        Assertions.assertEquals(appointmentRepository.findById(4L).get().getDate(), expectedDate);
    }

    @Test
    void appointmentDeleted() {
        appointmentRepository.deleteById(4L);
        Assertions.assertThrows(NoSuchElementException.class, () -> {
            appointmentRepository.findById(4L).get();
        });
    }
}